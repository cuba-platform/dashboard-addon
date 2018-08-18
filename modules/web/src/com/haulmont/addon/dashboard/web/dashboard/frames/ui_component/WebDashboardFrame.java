/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.frames.ui_component;

import com.haulmont.addon.dashboard.converter.JsonConverter;
import com.haulmont.addon.dashboard.entity.PersistentDashboard;
import com.haulmont.addon.dashboard.gui.components.DashboardFrame;
import com.haulmont.addon.dashboard.gui.components.WidgetBrowse;
import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.Parameter;
import com.haulmont.addon.dashboard.web.DashboardException;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame;
import com.haulmont.addon.dashboard.web.dashboard.tools.AccessConstraintsHelper;
import com.haulmont.addon.dashboard.web.events.DashboardUpdatedEvent;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.addon.dashboard.web.DashboardException;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame;
import com.haulmont.addon.dashboard.web.dashboard.tools.AccessConstraintsHelper;
import com.haulmont.addon.dashboard.web.events.DashboardUpdatedEvent;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame.DASHBOARD;
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
        refresh();
    }

    @Override
    public void refresh() {
        if (isNotBlank(jsonPath)) {
            updateDashboard(loadDashboardByJson(jsonPath));
        } else if (isNotBlank(referenceName)) {
            updateDashboard(loadDashboardByReferenceName(referenceName));
        }
    }

    //@EventListener TODO: listen to this event or implement it???
    public void onUpdateDashboard(DashboardUpdatedEvent event) {
        Dashboard source = event.getSource();

        if (source.getId().equals(canvasFrame.getDashboard().getId())) {
            updateDashboard(source);
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
