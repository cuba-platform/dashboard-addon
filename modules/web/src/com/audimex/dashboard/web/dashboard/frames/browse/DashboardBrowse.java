/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.frames.browse;

import com.audimex.dashboard.converter.JsonConverter;
import com.audimex.dashboard.entity.DashboardPersist;
import com.audimex.dashboard.model.Dashboard;
import com.audimex.dashboard.web.dashboard.frames.editor.DashboardEdit;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
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
    @Inject
    protected UserSessionSource sessionSource;

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

    protected boolean isAllowedOpenEditor(Dashboard dashboard) {
        String currentUserLogin = sessionSource.getUserSession().getUser().getLogin();
        return dashboard.getIsAvailableForAllUsers() || currentUserLogin.equals(dashboard.getCreatedBy());
    }

    protected void openDashboardEditor(Dashboard newItem) {
        if (!isAllowedOpenEditor(newItem)) {
            showNotification(getMessage("notOpenEditorDashboard"), NotificationType.WARNING);
            return;
        }

        DashboardEdit editor = (DashboardEdit) openEditor(DashboardEdit.SCREEN_NAME, newItem, THIS_TAB);
        editor.addCloseWithCommitListener(() -> {
            Dashboard result = editor.getDashboard();
            String jsonModel = converter.dashboardToJson(result);
            UUID dashId = result.getId();

            persDashboardsDs.refresh();
            Optional<DashboardPersist> persDashOpt = persDashboardsDs.getItems().stream()
                    .filter(item -> dashId.equals(item.getId()))
                    .findFirst();

            if (persDashOpt.isPresent()) {
                DashboardPersist persDash = persDashOpt.get();
                persDash.setDashboardModel(jsonModel);
                persDash.setReferenceName(result.getReferenceName());
                persDashboardsDs.updateItem(persDash);
            } else {
                DashboardPersist persDash = metadata.create(DashboardPersist.class);
                persDash.setId(dashId);
                persDash.setDashboardModel(jsonModel);
                persDash.setReferenceName(result.getReferenceName());
                persDashboardsDs.addItem(persDash);
            }


            persDashboardsDs.commit();
            persDashboardsDs.refresh();
        });
    }
}
