/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.dashboard;

import com.audimex.dashboard.entity.Dashboard;
import com.audimex.dashboard.web.model.DashboardModel;
import com.google.gson.Gson;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardEdit extends AbstractEditor<Dashboard> {
    public static final String DASHBOARD_PARAMETER = "DASHBOARD_PARAMETER";

    protected DashboardModel dashboardModel = null;
    protected Gson gson = new Gson();

    @Inject
    protected VBoxLayout dashboardDesignerVBox;

    @Inject
    protected FieldGroup fieldGroupLeft;

    @Inject
    protected Datasource<Dashboard> dashboardDs;

    @Inject
    protected Metadata metadata;

    @Inject
    protected UserSession userSession;

    @Inject
    private ComponentsFactory componentsFactory;

    protected DashboardFrame dashboardDesigner;

    protected LookupField metaLookupField;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        FieldGroup.FieldConfig metaConfig = fieldGroupLeft.getField("entityType");
        metaLookupField = componentsFactory.createComponent(LookupField.class);
        metaLookupField.setDatasource(dashboardDs, metaConfig.getProperty());
        metaConfig.setComponent(metaLookupField);

        Map<String, Object> metaClasses = new LinkedHashMap<>();
        metadata.getTools().getAllPersistentMetaClasses()
                .forEach(metaClass ->
                        metaClasses.put(metaClass.getName(), metaClass.getName())
                );
        metaLookupField.setOptionsMap(metaClasses);
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
}