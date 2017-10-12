/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.dashboard;

import com.audimex.dashboard.entity.Dashboard;
import com.audimex.dashboard.web.model.DashboardModel;
import com.google.gson.Gson;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardEdit extends AbstractEditor<Dashboard> {
    public static final String DASHBOARD_PARAMETER = "DASHBOARD_PARAMETER";
    public static final String DASHBOARD_ENTITY = "DASHBOARD_ENTITY";
    public static final String DASHBOARD_OUTER_PARAMS = "DASHBOARD_OUTER_PARAMS";

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

    @WindowParam(name = DASHBOARD_ENTITY)
    protected Entity entity;

    @WindowParam(name = DASHBOARD_OUTER_PARAMS)
    protected Map<String, Object> outerParameters;

    protected DashboardFrame dashboardDesigner;

    protected LookupField metaLookupField;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        this.setId("dashboard_edit");

        FieldGroup.FieldConfig metaConfig = fieldGroupLeft.getField("entityType");
        metaLookupField = componentsFactory.createComponent(LookupField.class);
        metaLookupField.setDatasource(dashboardDs, metaConfig.getProperty());
        metaConfig.setComponent(metaLookupField);

        Map<String, Object> metaClasses = new LinkedHashMap<>();
        metadata.getTools().getAllPersistentMetaClasses()
                .forEach(metaClass ->
                        metaClasses.put(metaClass.getJavaClass().getSimpleName(), metaClass.getName())
                );
        metaLookupField.setOptionsMap(metaClasses);
    }

    @Override
    protected void postInit() {
        super.postInit();

        initDashboardFrame();

        metaLookupField.addValueChangeListener(event -> {
            getItem().setModel(null);
            initDashboardFrame();
        });
    }

    protected void initDashboardFrame() {
        dashboardDesigner = (DashboardFrame) openFrame(dashboardDesignerVBox, "dashboard-frame",
                ParamsMap.of(
                        DASHBOARD_PARAMETER, getItem(),
                        DASHBOARD_OUTER_PARAMS, outerParameters
                )
        );
        dashboardDesigner.setId("dashboardDesigner");

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