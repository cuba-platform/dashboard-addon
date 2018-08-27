/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.frames.ui_component;

import com.haulmont.addon.dashboard.model.Parameter;
import com.haulmont.addon.dashboard.web.dashboard.converter.JsonConverter;
import com.haulmont.addon.dashboard.entity.PersistentDashboard;
import com.haulmont.addon.dashboard.gui.components.DashboardFrame;
import com.haulmont.addon.dashboard.gui.components.WidgetBrowse;
import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.web.DashboardException;
import com.haulmont.addon.dashboard.web.dashboard.assistant.DashboardViewAssistant;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame;
import com.haulmont.addon.dashboard.web.dashboard.tools.AccessConstraintsHelper;
import com.haulmont.addon.dashboard.web.events.DashboardEvent;
import com.haulmont.addon.dashboard.web.events.DashboardUpdatedEvent;
import com.haulmont.addon.dashboard.web.widget_types.RefreshableWidget;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.gui.components.*;

import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ReflectionUtils;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.haulmont.addon.dashboard.web.dashboard.frames.browse.DashboardView.REFERENCE_NAME;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class WebDashboardFrame extends AbstractFrame implements DashboardFrame {

    public static final String SCREEN_NAME = "dashboardComponent";

    @Inject
    protected VBoxLayout canvasBox;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected JsonConverter jsonConverter;
    @Inject
    protected ResourceLoader resourceLoader;
    @Inject
    protected Metadata metadata;
    @Inject
    protected AccessConstraintsHelper accessHelper;
    @Inject
    protected Messages messages;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected Events events;

    protected DashboardViewAssistant dashboardViewAssistant;

    protected CanvasFrame canvasFrame;

    protected String referenceName;
    protected String jsonPath;
    protected Integer timerDelay = 0;
    protected List<Parameter> xmlParameters = new ArrayList<>();
    protected String assistantBeanName;
    protected Dashboard dashboard;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        setReferenceName(StringUtils.isEmpty(referenceName) ? (String) params.getOrDefault(REFERENCE_NAME, "") : referenceName);
        refresh();
        initAssistant(this);
        initTimer(this.getParent());
    }

    @Override
    public void refresh() {
        if (isNotBlank(jsonPath)) {
            updateDashboard(loadDashboardByJson(jsonPath));
        } else if (isNotBlank(referenceName)) {
            updateDashboard(loadDashboardByReferenceName(referenceName));
        }
    }

    private void initAssistant(WebDashboardFrame frame) {
        String assistantBeanName = StringUtils.isEmpty(getAssistantBeanName()) ? dashboard.getAssistantBeanName() : getAssistantBeanName();
        if (StringUtils.isNotEmpty(assistantBeanName)) {
            DashboardViewAssistant assistantBean = AppBeans.get(assistantBeanName, DashboardViewAssistant.class);
            BeanFactory bf = ((AbstractApplicationContext) AppContext.getApplicationContext()).getBeanFactory();
            if (assistantBean != null && bf.isPrototype(assistantBeanName)) {
                assistantBean.init(frame);
                dashboardViewAssistant = assistantBean;
            }
        }
    }

    private void initTimer(Component frame) {
        Window parentWindow = (Window) frame;
        int timerDelay = getTimerDelay() == 0 ? dashboard.getTimerDelay() : getTimerDelay();
        if (timerDelay > 0) {
            Timer dashboardUpdatedTimer = componentsFactory.createTimer();
            parentWindow.addTimer(dashboardUpdatedTimer);
            dashboardUpdatedTimer.setDelay(timerDelay * 1000);
            dashboardUpdatedTimer.setRepeating(true);
            dashboardUpdatedTimer.addActionListener(timer -> events.publish(new DashboardUpdatedEvent(dashboard)));
            dashboardUpdatedTimer.start();
        }
    }

    @SuppressWarnings("unchecked")
    @EventListener
    public void dashboardEventListener(DashboardEvent dashboardEvent) throws InvocationTargetException, IllegalAccessException {
        refreshWidgets(dashboardEvent);

        if (dashboardViewAssistant == null) {
            return;
        }
        Class eventClass = dashboardEvent.getClass();
        Method[] methods = ReflectionUtils.getUniqueDeclaredMethods(dashboardViewAssistant.getClass());
        List<Method> eventListenerMethods = Arrays.stream(methods)
                .filter(m -> m.getAnnotation(EventListener.class) != null)
                .filter(m -> m.getParameterCount() == 1)
                .collect(Collectors.toList());

        for (Method method : eventListenerMethods) {
            java.lang.reflect.Parameter[] parameters = method.getParameters();
            java.lang.reflect.Parameter parameter = parameters[0];
            Class methodEventTypeArg = parameter.getType();
            if (methodEventTypeArg.isAssignableFrom(eventClass)) {
                method.invoke(dashboardViewAssistant, dashboardEvent);

            }
        }


    }

    @Override
    public WidgetBrowse getWidgetBrowse(String widgetId) {
        return canvasFrame.getWidgetBrowse(widgetId);
    }

    protected void updateDashboard(Dashboard dashboard) {
        if (dashboard == null) {
            showNotification(messages.getMainMessage("notLoadedDashboard"), NotificationType.ERROR);
            return;
        }

        if (!accessHelper.isDashboardAllowedCurrentUser(dashboard)) {
            showNotification(messages.getMainMessage("notOpenBrowseDashboard"), NotificationType.WARNING);
            return;
        }

        setCaption(dashboard.getTitle());
        addXmlParameters(dashboard);
        updateCanvasFrame(dashboard);
        this.dashboard = dashboard;
    }

    protected Dashboard loadDashboardByJson(String jsonPath) {
        Resource jsonRes = resourceLoader.getResource(format("classpath:%s", jsonPath));
        if (!jsonRes.exists()) {
            throw new DashboardException(format("There isn't the json file by the path: %s", jsonPath));
        }

        try {
            String json = IOUtils.toString(jsonRes.getInputStream(), UTF_8);
            return jsonConverter.dashboardFromJson(json);
        } catch (Exception e) {
            throw new DashboardException(format("Error reading the json by the path: %s", jsonPath), e);
        }
    }

    protected Dashboard loadDashboardByReferenceName(String referenceName) {
        LoadContext<PersistentDashboard> loadContext = LoadContext.create(PersistentDashboard.class)
                .setQuery(LoadContext.createQuery("select d from amxd$PersistentDashboard d where d.referenceName = :referenceName")
                        .setParameter("referenceName", referenceName))
                .setView("_local");

        PersistentDashboard entity = dataManager.load(loadContext);
        if (entity == null || entity.getDashboardModel() == null) {
            return null;
        }
        return jsonConverter.dashboardFromJson(entity.getDashboardModel());
    }

    protected void addXmlParameters(Dashboard dashboard) {
        List<Parameter> parameters = dashboard.getParameters();
        parameters.removeAll(getDuplicatesParams(dashboard));
        parameters.addAll(xmlParameters);
    }

    protected List<Parameter> getDuplicatesParams(Dashboard dashboard) {
        return dashboard.getParameters().stream()
                .filter(param -> xmlParameters.stream()
                        .anyMatch(xmlParameter -> param.getName().equals(xmlParameter.getName())))
                .collect(Collectors.toList());
    }

    protected void updateCanvasFrame(Dashboard dashboard) {
        if (canvasFrame == null) {
            canvasFrame = (CanvasFrame) openFrame(canvasBox, CanvasFrame.SCREEN_NAME, ParamsMap.of(
                    CanvasFrame.DASHBOARD, dashboard
            ));
        } else {
            canvasFrame.updateLayout(dashboard);
        }
    }

    protected void refreshWidgets(DashboardEvent dashboardEvent) {
        List<WidgetBrowse> wbs = canvasFrame.getRefreshableWidgets();
        for (WidgetBrowse wb : wbs) {
            RefreshableWidget rw = (RefreshableWidget) wb;
            rw.refresh(dashboardEvent);
        }
    }

    @Override
    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    @Override
    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    @Override
    public void setXmlParameters(List<Parameter> parameters) {
        xmlParameters = parameters;
    }

    @Override
    public void setTimerDelay(int delay) {
        this.timerDelay = delay;
    }

    @Override
    public int getTimerDelay() {
        return timerDelay;
    }

    @Override
    public Dashboard getDashboard() {
        return dashboard;
    }

    @Override
    public String getAssistantBeanName() {
        return assistantBeanName;
    }

    @Override
    public void setAssistantBeanName(String assistantBeanName) {
        this.assistantBeanName = assistantBeanName;
    }
}
