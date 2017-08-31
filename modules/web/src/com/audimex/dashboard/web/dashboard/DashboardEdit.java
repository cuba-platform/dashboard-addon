/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.dashboard;

import com.audimex.dashboard.entity.Dashboard;
import com.audimex.dashboard.web.model.DashboardModel;
import com.google.gson.Gson;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.security.global.UserSession;

import javax.inject.Inject;
import java.util.Map;

public class DashboardEdit extends AbstractEditor<Dashboard> {
    public static final String DASHBOARD_PARAMETER = "DASHBOARD_PARAMETER";

    DashboardModel dashboardModel = null;
    Gson gson = new Gson();

    @Inject
    private VBoxLayout dashboardDesignerVBox;

    @Inject
    private UserSession userSession;

    protected DashboardFrame dashboardDesigner;

    protected CommitContext commitContext = new CommitContext();

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected void postInit() {
        super.postInit();

        dashboardDesigner = (DashboardFrame) openFrame(dashboardDesignerVBox, "dashboard-frame",
                ParamsMap.of(DASHBOARD_PARAMETER, getItem())
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
}