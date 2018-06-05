/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.frames.ui_component;

import com.audimex.dashboard.converter.JsonConverter;
import com.audimex.dashboard.entity.PersistentDashboard;
import com.audimex.dashboard.gui.components.DashboardFrame;
import com.audimex.dashboard.model.Dashboard;
import com.audimex.dashboard.model.Parameter;
import com.audimex.dashboard.web.events.DashboardUpdatedEvent;
import com.audimex.dashboard.web.dashboard.frames.canvas.CanvasFrame;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.VBoxLayout;
import org.apache.commons.io.IOUtils;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.audimex.dashboard.web.dashboard.frames.canvas.CanvasFrame.DASHBOARD;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class WebDashboardFrame extends AbstractFrame implements DashboardFrame {
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

    protected CanvasFrame canvasFrame;

    protected String referenceName;
    protected String jsonPath;
    protected List<Parameter> xmlParameters = new ArrayList<>();

    protected Dashboard dashboard;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        refresh();
    }

    @Override
    public void refresh() {
        if (isNotBlank(jsonPath)) {
            dashboard = loadDashboardByJson(jsonPath);
        } else if (referenceName != null) {
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

    protected void updateDashboard(Dashboard dashboard) {
        if (dashboard == null || !isAllowed(dashboard)) {
            showNotification(messages.getMainMessage("notOpenBrowseDashboard"), NotificationType.WARNING);
            canvasFrame = null;
            canvasBox.removeAll();
        } else {
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
            throw new RuntimeException(format("There isn't the json file by the path: %s", jsonPath));
        }

        try {
            String json = IOUtils.toString(jsonRes.getInputStream(), UTF_8);
            return jsonConverter.dashboardFromJson(json);
        } catch (Exception e) {
            throw new RuntimeException(format("Error reading the json by the path: %s", jsonPath), e);
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
}
