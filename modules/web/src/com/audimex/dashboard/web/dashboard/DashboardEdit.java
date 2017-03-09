/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.dashboard;

import com.audimex.dashboard.entity.Dashboard;
import com.audimex.dashboard.web.model.DashboardModel;
import com.google.gson.Gson;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.security.global.UserSession;

import javax.inject.Inject;

public class DashboardEdit extends AbstractEditor<Dashboard> {
    DashboardModel dashboardModel = null;
    Gson gson = new Gson();

    @Inject
    private DashboardFrame dashboardDesigner;

    @Inject
    private UserSession userSession;

    @Override
    protected void postInit() {
        super.postInit();

        if (getItem().getModel() != null) {
            dashboardModel = gson.fromJson(getItem().getModel(), DashboardModel.class);
            dashboardDesigner.setDashboardModel(dashboardModel);
            dashboardDesigner.refresh();
        }
    }

    @Override
    protected boolean preCommit() {
        String dashboardJSON = gson.toJson(dashboardDesigner.getDashboardModel());
        getItem().setModel(dashboardJSON);
        getItem().setUser(userSession.getUser());

        return super.preCommit();
    }
}