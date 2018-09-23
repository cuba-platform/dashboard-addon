/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.frames.browse;

import com.haulmont.addon.dashboard.model.visualmodel.RootLayout;
import com.haulmont.addon.dashboard.web.dashboard.converter.JsonConverter;
import com.haulmont.addon.dashboard.entity.PersistentDashboard;
import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.DashboardEdit;
import com.haulmont.addon.dashboard.web.dashboard.tools.AccessConstraintsHelper;
import com.haulmont.addon.dashboard.web.events.DashboardUpdatedEvent;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.LookupComponent;
import com.haulmont.cuba.gui.components.SelectAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.haulmont.cuba.gui.WindowManager.OpenType.NEW_WINDOW;
import static com.haulmont.cuba.gui.WindowManager.OpenType.THIS_TAB;
import static org.apache.commons.collections4.CollectionUtils.emptyCollection;
import static org.springframework.util.CollectionUtils.isEmpty;

public class PersistentDashboardBrowse extends AbstractLookup {
    @Inject
    protected CollectionDatasource<PersistentDashboard, UUID> persDashboardsDs;
    @Inject
    protected CollectionDatasource<Dashboard, UUID> modelDashboardsDs;
    @Inject
    protected JsonConverter converter;
    @Inject
    protected Metadata metadata;
    @Inject
    protected Events events;

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
        for (PersistentDashboard persDash : persDashboardsDs.getItems()) {
            Dashboard model = converter.dashboardFromJson(persDash.getDashboardModel());
            modelDashboardsDs.includeItem(model);
        }
    }

    public void createDashboard() {
        Dashboard dashboard = metadata.create(Dashboard.class);
        dashboard.setVisualModel(metadata.create(RootLayout.class));
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

    public void viewDashboard() {
        Dashboard item = modelDashboardsDs.getItem();
        if (item != null) {
            openWindow(DashboardView.SCREEN_NAME, NEW_WINDOW, ParamsMap.of(
                    DashboardView.REFERENCE_NAME, item.getReferenceName(),
                    DashboardView.DISPLAY_NAME, item.getTitle()
            ));
        }
    }

    protected void openDashboardEditor(Dashboard newItem) {
        DashboardEdit editor = (DashboardEdit) openEditor(DashboardEdit.SCREEN_NAME, newItem, THIS_TAB);
        editor.addCloseWithCommitListener(() -> {
            Dashboard result = editor.getItem();
            String jsonModel = converter.dashboardToJson(result);
            UUID dashId = result.getId();

            persDashboardsDs.refresh();
            Optional<PersistentDashboard> persDashOpt = persDashboardsDs.getItems().stream()
                    .filter(item -> dashId.equals(item.getId()))
                    .findFirst();

            if (persDashOpt.isPresent()) {
                PersistentDashboard persDash = persDashOpt.get();
                persDash.setDashboardModel(jsonModel);
                persDash.setReferenceName(result.getReferenceName());
                persDash.setIsAvailableForAllUsers(result.getIsAvailableForAllUsers());
                persDashboardsDs.updateItem(persDash);
            } else {
                PersistentDashboard persDash = metadata.create(PersistentDashboard.class);
                persDash.setId(dashId);
                persDash.setDashboardModel(jsonModel);
                persDash.setReferenceName(result.getReferenceName());
                persDash.setIsAvailableForAllUsers(result.getIsAvailableForAllUsers());
                persDashboardsDs.addItem(persDash);
            }

            persDashboardsDs.commit();
            persDashboardsDs.refresh();
            events.publish(new DashboardUpdatedEvent(result));
        });
        editor.addCloseListener((actionId) -> {
            if (!"commit".equals(actionId)) {
                updateTable();
            }
        });
    }
}
