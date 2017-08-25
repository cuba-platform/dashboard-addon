/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.dashboard;

import com.audimex.dashboard.entity.Dashboard;
import com.audimex.dashboard.entity.DashboardWidgetLink;
import com.audimex.dashboard.entity.WidgetParameter;
import com.audimex.dashboard.web.dashboard.events.DashboardEvent;
import com.audimex.dashboard.web.dashboard.events.DashboardEventType;
import com.audimex.dashboard.web.model.DashboardModel;
import com.google.gson.Gson;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.security.global.UserSession;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class DashboardEdit extends AbstractEditor<Dashboard> {
    public static final String DASHBOARD_PARAMETER = "DASHBOARD_PARAMETER";
    public static final String DASHBOARD_EVENT_ACTION = "DASHBOARD_EVENT_ACTION";

    DashboardModel dashboardModel = null;
    Gson gson = new Gson();

    @Inject
    private VBoxLayout dashboardDesignerVBox;

    @Inject
    private UserSession userSession;

    @Inject
    protected CollectionDatasource<DashboardWidgetLink, UUID> linksDs;

    @Inject
    protected CollectionDatasource<WidgetParameter, UUID> parametersDs;

    protected DashboardFrame dashboardDesigner;

    protected Consumer<DashboardEvent> dashboardEventExecutor = this::eventExecution;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected void postInit() {
        super.postInit();

        dashboardDesigner = (DashboardFrame) openFrame(dashboardDesignerVBox, "dashboard-frame",
                ParamsMap.of(
                        DASHBOARD_PARAMETER, getItem(),
                        DASHBOARD_EVENT_ACTION, dashboardEventExecutor
                )
        );

        if (getItem().getModel() != null) {
            dashboardModel = gson.fromJson(getItem().getModel(), DashboardModel.class);
            dashboardDesigner.setDashboardModel(dashboardModel);
        }
    }

    @Override
    protected boolean preCommit() {
        String dashboardJSON = gson.toJson(dashboardDesigner.getDashboardModel());
        getItem().setModel(dashboardJSON);
        getItem().setUser(userSession.getUser());

        return super.preCommit();
    }

    protected void eventExecution(DashboardEvent event) {
        Entity entity = (Entity) event.getEntity();
        DashboardEventType type = event.getType();

        if (entity instanceof DashboardWidgetLink) {
            executeEventAction(type, linksDs, entity);
        } else if (entity instanceof WidgetParameter) {
            executeEventAction(type, parametersDs, entity);
        }
    }

    @SuppressWarnings("unchecked")
    protected void executeEventAction(DashboardEventType type, CollectionDatasource datasource, Entity entity) {
        switch (type) {
            case CREATE:
                datasource.addItem(entity);
                break;
            case UPDATE:
                datasource.updateItem(entity);
                break;
            case REMOVE:
                datasource.removeItem(entity);
                break;
            default:
        }
    }
}