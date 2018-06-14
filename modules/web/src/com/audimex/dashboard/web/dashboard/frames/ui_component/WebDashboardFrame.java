/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.frames.ui_component;

import com.audimex.dashboard.converter.JsonConverter;
import com.audimex.dashboard.entity.PersistentDashboard;
import com.audimex.dashboard.gui.components.DashboardFrame;
import com.audimex.dashboard.model.Dashboard;
import com.audimex.dashboard.model.Parameter;
import com.audimex.dashboard.web.DashboardException;
import com.audimex.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame;
import com.audimex.dashboard.web.events.DashboardUpdatedEvent;
import com.audimex.dashboard.web.widget_types.WidgetBrowse;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Timer;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.apache.commons.io.IOUtils;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.audimex.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame.DASHBOARD;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class WebDashboardFrame extends AbstractWindow implements DashboardFrame {
    public static final String SCREEN_NAME = "dashboardComponent";
    public static final String REFERENCE_NAME = "REFERENCE_NAME";

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
    protected UserSessionSource sessionSource;
    @Inject
    protected Messages messages;
    @Inject
    protected ComponentsFactory factory;

    protected CanvasFrame canvasFrame;
    protected Timer timer;

    protected String referenceName;
    protected String jsonPath;
    protected int timerDelay = -1;
    protected List<Parameter> xmlParameters = new ArrayList<>();

    protected Dashboard dashboard;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        setParams(params);
        refresh();

        if (timerDelay > 0) {
            timer = factory.createTimer();
            addTimer(timer);

            timer.setDelay(timerDelay);
            timer.setRepeating(true);
            timer.addActionListener(t -> refresh());
            timer.start();
        }
    }

    @Override
    public void refresh() {
        if (isNotBlank(jsonPath)) {
            dashboard = loadDashboardByJson(jsonPath);
        } else if (isNotBlank(referenceName)) {
            dashboard = loadDashboardByReferenceName(referenceName);
        }
        updateDashboard(dashboard);
    }

    @EventListener
    public void onUpdateDashboard(DashboardUpdatedEvent event) {
        Dashboard source = event.getSource();

        if (source.getId().equals(dashboard.getId())) {
            updateDashboard(source);
        }
    }

    public WidgetBrowse getWidgetBrowse(String widgetId) {
        return canvasFrame.getWidgetBrowse(widgetId);
    }

    protected void setParams(Map<String, Object> params) {
        referenceName = (String) params.getOrDefault(REFERENCE_NAME, "");
    }

    protected void updateDashboard(Dashboard dashboard) {
        if (dashboard == null || !isAllowed(dashboard)) {
            showNotification(messages.getMainMessage("notOpenBrowseDashboard"), NotificationType.WARNING);
            canvasFrame = null;
            canvasBox.removeAll();
        } else {
            setCaption(dashboard.getTitle());
            addXmlParameters(dashboard);
            updateCanvasFrame(dashboard);
        }
    }

    protected boolean isAllowed(Dashboard dashboard) {
        String currentUserLogin = sessionSource.getUserSession().getUser().getLogin();
        return dashboard.getIsAvailableForAllUsers() || currentUserLogin.equals(dashboard.getCreatedBy());
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
                    DASHBOARD, dashboard
            ));
        } else {
            canvasFrame.updateLayout(dashboard);
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
        timerDelay = delay;
    }
}
