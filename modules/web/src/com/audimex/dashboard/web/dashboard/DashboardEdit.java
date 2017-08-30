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
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
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

    protected DashboardFrame dashboardDesigner;

    protected Consumer<DashboardEvent> dashboardEventExecutor = this::eventExecution;

    protected CommitContext commitContext = new CommitContext();

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

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        DataManager dataManager = AppBeans.get(DataManager.class);
        dataManager.commit(commitContext);

        return super.postCommit(committed, close);
    }

    protected void eventExecution(DashboardEvent event) {
        Entity entity = (Entity) event.getEntity();
        DashboardEventType type = event.getType();

        switch (type) {
            case CREATE:
                commitContext.addInstanceToCommit(entity);
                break;
            case UPDATE:
                commitContext.addInstanceToCommit(entity);
                break;
            case REMOVE:
                commitContext.addInstanceToRemove(entity);
                break;
            default:
        }
    }
}