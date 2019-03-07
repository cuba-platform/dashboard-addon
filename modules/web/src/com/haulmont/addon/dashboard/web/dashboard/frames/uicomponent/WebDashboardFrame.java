/*
 * Copyright (c) 2008-2018 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.haulmont.addon.dashboard.web.dashboard.frames.uicomponent;

import com.haulmont.addon.dashboard.entity.PersistentDashboard;
import com.haulmont.addon.dashboard.gui.components.DashboardFrame;
import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.Parameter;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.web.DashboardException;
import com.haulmont.addon.dashboard.web.dashboard.assistant.DashboardViewAssistant;
import com.haulmont.addon.dashboard.web.dashboard.converter.JsonConverter;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasLayout;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasWidgetLayout;
import com.haulmont.addon.dashboard.web.dashboard.tools.AccessConstraintsHelper;
import com.haulmont.addon.dashboard.web.events.DashboardEvent;
import com.haulmont.addon.dashboard.web.events.DashboardUpdatedEvent;
import com.haulmont.addon.dashboard.web.events.ItemsSelectedEvent;
import com.haulmont.addon.dashboard.web.widget.LookupWidget;
import com.haulmont.addon.dashboard.web.widget.RefreshableWidget;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.gui.Fragments;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.screen.MapScreenOptions;
import com.haulmont.cuba.gui.screen.ScreenFragment;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame.DASHBOARD_FRAME;
import static com.haulmont.addon.dashboard.web.dashboard.frames.view.DashboardView.CODE;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UiController("dashboard$DashboardComponent")
@UiDescriptor("web-dashboard-frame.xml")
public class WebDashboardFrame extends AbstractFrame implements DashboardFrame {

    public static final String SCREEN_NAME = "dashboard$DashboardComponent";

    private Logger log = LoggerFactory.getLogger(WebDashboardFrame.class);

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
    protected UiComponents componentsFactory;
    @Inject
    protected Events events;
    @Inject
    protected Notifications notifications;
    @Inject
    protected Fragments fragments;

    protected DashboardViewAssistant dashboardViewAssistant;

    protected CanvasFrame canvasFrame;

    protected String code;
    protected String jsonPath;
    protected Integer timerDelay = 0;
    protected List<Parameter> xmlParameters = new ArrayList<>();
    protected String assistantBeanName;
    protected Dashboard dashboard;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        setCode(StringUtils.isEmpty(code) ? (String) params.getOrDefault(CODE, "") : code);
        refresh();
        initAssistant(this);
        initTimer(this.getParent());
        initLookupWidget();
    }

    private void initLookupWidget() {
        List<LookupWidget> lookupWidgets = canvasFrame.getLookupWidgets();
        for (LookupWidget widget : lookupWidgets) {
            assignItemSelectedHandler(widget);
        }
    }

    private void assignItemSelectedHandler(LookupWidget widget) {
        ListComponent lookupComponent = widget.getLookupComponent();
        lookupComponent.getItems().addStateChangeListener(e -> {
            events.publish(new ItemsSelectedEvent((Widget) widget, lookupComponent.getSelected()));
        });
    }

    @Override
    public void refresh() {
        if (isNotBlank(jsonPath)) {
            updateDashboard(loadDashboardByJson(jsonPath));
        } else if (isNotBlank(code)) {
            updateDashboard(loadDashboardByCode(code));
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
        Window parentWindow = findWindow(frame);
        int timerDelay = getTimerDelay() == 0 ? dashboard.getTimerDelay() : getTimerDelay();
        if (timerDelay > 0 && parentWindow != null) {
            Timer dashboardUpdatedTimer = componentsFactory.create(Timer.class);
            parentWindow.addTimer(dashboardUpdatedTimer);
            dashboardUpdatedTimer.setDelay(timerDelay * 1000);
            dashboardUpdatedTimer.setRepeating(true);
            dashboardUpdatedTimer.addTimerActionListener(timer -> events.publish(new DashboardUpdatedEvent(dashboard)));
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

    protected void updateDashboard(Dashboard dashboard) {
        if (dashboard == null) {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption(messages.getMainMessage("notLoadedDashboard"))
                    .show();
            return;
        }

        if (!accessHelper.isDashboardAllowedCurrentUser(dashboard)) {
            notifications.create(Notifications.NotificationType.WARNING)
                    .withCaption(messages.getMainMessage("notOpenBrowseDashboard"))
                    .show();
            return;
        }

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

    protected Dashboard loadDashboardByCode(String code) {
        LoadContext<PersistentDashboard> loadContext = LoadContext.create(PersistentDashboard.class)
                .setQuery(LoadContext.createQuery("select d from dashboard$PersistentDashboard d where d.code = :code")
                        .setParameter("code", code))
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
            canvasFrame = fragments.create(this, CanvasFrame.class, new MapScreenOptions(ParamsMap.of(
                    CanvasFrame.DASHBOARD, dashboard, DASHBOARD_FRAME, this
            )));
            canvasFrame.init();
            canvasBox.removeAll();
            canvasBox.add(canvasFrame.getFragment());
        } else {
            canvasFrame.updateLayout(dashboard);
        }
    }

    protected void refreshWidgets(DashboardEvent dashboardEvent) {
        List<RefreshableWidget> rws = canvasFrame.getRefreshableWidgets();
        for (RefreshableWidget rw : rws) {
            rw.refresh(dashboardEvent);
        }
    }

    protected Window findWindow(Component frame) {
        Component parent = frame;
        while (parent != null) {
            if (parent instanceof Window) {
                return (Window) parent;
            } else {
                parent = parent.getParent();
            }
        }
        log.error(messages.getMessage(getClass(), "dashboard.noWindow"));
        return null;
    }

    public ScreenFragment getWidget(String widgetId) {
        return searchWidgetFrame(canvasFrame.getvLayout(), widgetId);
    }

    protected ScreenFragment searchWidgetFrame(CanvasLayout layout, String widgetId) {
        if (CanvasWidgetLayout.class.isAssignableFrom(layout.getClass())) {
            CanvasWidgetLayout canvasWidgetLayout = (CanvasWidgetLayout) layout;
            if (widgetId.equals(canvasWidgetLayout.getWidget().getWidgetId())) {
                return (ScreenFragment) canvasWidgetLayout.getWidgetComponent();
            }
            return null;
        }

        for (Component child : layout.getDelegate().getOwnComponents()) {
            ScreenFragment result = searchWidgetFrame((CanvasLayout) child, widgetId);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
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
