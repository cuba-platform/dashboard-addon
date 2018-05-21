/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard;

import com.audimex.dashboard.converter.JsonConverter;
import com.audimex.dashboard.entity.DashboardPersist;
import com.audimex.dashboard.model.Dashboard;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.haulmont.cuba.gui.WindowManager.OpenType.THIS_TAB;

public class DashboardBrowse extends AbstractLookup {
    @Inject
    protected CollectionDatasource<DashboardPersist, UUID> persDashboardsDs;
    @Inject
    protected CollectionDatasource<Dashboard, UUID> modelDashboardsDs;
    @Inject
    protected JsonConverter converter;
    @Inject
    protected Metadata metadata;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initDs();
    }

    protected void initDs() {
        persDashboardsDs.addCollectionChangeListener(e -> updateTable());
        persDashboardsDs.refresh();
    }

    protected void updateTable() {
        modelDashboardsDs.clear();
        for (DashboardPersist persDash : persDashboardsDs.getItems()) {
            Dashboard model = converter.dashboardFromJson(persDash.getDashboardModel());
            modelDashboardsDs.includeItem(model);
        }
    }

    public void createDashboard() {
        Dashboard dashboard = metadata.create(Dashboard.class);
        openDashboardEditor(dashboard);
    }

    public void editDashboard() {
        if (modelDashboardsDs.getItem() != null) {
            openDashboardEditor(modelDashboardsDs.getItem());
        }
    }

    public void removeDashboard() {
        if (modelDashboardsDs.getItem() != null) {
            UUID modelId = modelDashboardsDs.getItem().getId();

            persDashboardsDs.getItems().stream()
                    .filter(item -> modelId.equals(item.getId()))
                    .findFirst()
                    .ifPresent(item -> {
                        persDashboardsDs.removeItem(item);
                        persDashboardsDs.commit();
                        persDashboardsDs.refresh();
                    });
        }
    }

    protected void openDashboardEditor(Dashboard dashboard) {
        AbstractEditor editor = openEditor("dashboardEdit", dashboard, THIS_TAB, modelDashboardsDs);
        editor.addCloseListener(actionId -> {
            persDashboardsDs.refresh();
        });
    }
}
