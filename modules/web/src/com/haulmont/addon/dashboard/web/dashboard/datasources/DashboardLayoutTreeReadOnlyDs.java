/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.addon.dashboard.web.dashboard.datasources;

import com.haulmont.addon.dashboard.model.visualmodel.*;
import com.haulmont.cuba.gui.data.impl.CustomHierarchicalDatasource;

import java.util.*;
import java.util.stream.Collectors;

import static com.haulmont.addon.dashboard.utils.DashboardLayoutUtils.findLayout;
import static com.haulmont.addon.dashboard.utils.DashboardLayoutUtils.findParentLayout;

public class DashboardLayoutTreeReadOnlyDs extends CustomHierarchicalDatasource<DashboardLayout, UUID> {

    private DashboardLayout visualModel;

    @Override
    protected Collection<DashboardLayout> getEntities(Map<String, Object> params) {
        ArrayList<DashboardLayout> al = new ArrayList<>();
        al.add(visualModel);
        return al;
    }

    @Override
    public Collection<UUID> getChildren(UUID itemId) {
        DashboardLayout item = findLayout(getRootLayout(), itemId);
        List<UUID> al = new ArrayList<>();
        if (item instanceof GridLayout) {
            GridLayout gridLayout = (GridLayout) item;
            al.addAll(gridLayout.getAreas().stream()
                    .sorted(Comparator.comparingInt(GridArea::getRow)
                            .thenComparingInt(GridArea::getCol))
                    .map(GridArea::getComponent)
                    .map(DashboardLayout::getId)
                    .collect(Collectors.toList()));
        } else if (item instanceof ResponsiveLayout) {
            ResponsiveLayout dashboardResponsiveLayout = (ResponsiveLayout) item;
            al.addAll(dashboardResponsiveLayout.getAreas().stream()
                    .sorted(Comparator.comparingInt(ResponsiveArea::getOrder))
                    .map(ResponsiveArea::getComponent)
                    .map(DashboardLayout::getId)
                    .collect(Collectors.toList()));
        } else {
            al.addAll(item.getChildren().stream()
                    .map(DashboardLayout::getId)
                    .collect(Collectors.toList()));
        }
        return al;
    }

    @Override
    public UUID getParent(UUID itemId) {
        DashboardLayout item = findLayout(getRootLayout(), itemId);
        DashboardLayout root = getItems().stream()
                .findFirst()
                .orElse(null);
        if (item == null || root == null || Objects.equals(root.getId(), item.getId())) {
            return null;
        } else {
            DashboardLayout parent = findParentLayout(root, item);
            return parent == null ? null : parent.getUuid();
        }
    }

    @Override
    public Collection<UUID> getRootItemIds() {
        List<UUID> al = new ArrayList<>(1);
        al.add(getItems().stream()
                .findFirst()
                .map(DashboardLayout::getId)
                .orElse(null));
        return al;
    }

    @Override
    public boolean isRoot(UUID itemId) {
        UUID root = getItems().stream()
                .findFirst()
                .map(DashboardLayout::getId)
                .orElse(null);
        return itemId.equals(root);
    }

    @Override
    public boolean hasChildren(UUID itemId) {
        DashboardLayout item = findLayout(getRootLayout(), itemId);
        if (item instanceof GridLayout) {
            GridLayout gridLayout = (GridLayout) item;
            return gridLayout.getAreas().size() > 0;
        } else if (item instanceof ResponsiveLayout) {
            ResponsiveLayout dashboardResponsiveLayout = (ResponsiveLayout) item;
            return dashboardResponsiveLayout.getAreas().size() > 0;
        } else {
            return item != null && item.getChildren().size() > 0;
        }
    }

    private DashboardLayout getRootLayout() {
        return getItems().stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public DashboardLayout getItem(UUID id) {
        return findLayout(getRootLayout(), id);
    }

    @Override
    public DashboardLayout getItemNN(UUID id) {
        return findLayout(getRootLayout(), id);
    }

    public void setVisualModel(DashboardLayout visualModel) {
        this.visualModel = visualModel;
    }

    public DashboardLayout getVisualModel() {
        return visualModel;
    }

    @Override
    public boolean containsItem(UUID itemId) {
        DashboardLayout dashboardLayout = findLayout(visualModel, itemId);
        return dashboardLayout != null;
    }

    @Override
    protected void loadData(Map<String, Object> params) {
        super.loadData(params);
    }
}
