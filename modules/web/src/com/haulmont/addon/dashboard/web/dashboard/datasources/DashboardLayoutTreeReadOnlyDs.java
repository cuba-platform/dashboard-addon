package com.haulmont.addon.dashboard.web.dashboard.datasources;

import com.haulmont.addon.dashboard.model.visual_model.*;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.data.impl.CustomHierarchicalDatasource;

import java.util.*;
import java.util.stream.Collectors;

import static com.haulmont.addon.dashboard.web.dashboard.datasources.DashboardLayoutUtils.findLayout;
import static com.haulmont.addon.dashboard.web.dashboard.datasources.DashboardLayoutUtils.findParentLayout;

public class DashboardLayoutTreeReadOnlyDs extends CustomHierarchicalDatasource<DashboardLayout, UUID> {

    private DashboardLayout visualModel;

    private Metadata metadata = AppBeans.get(Metadata.class);

    @Override
    protected Collection<DashboardLayout> getEntities(Map<String, Object> params) {
        ArrayList<DashboardLayout> al = new ArrayList<>();
        al.add(visualModel == null ? metadata.create(VerticalLayout.class) : visualModel);
        return al;
    }

    @Override
    public Collection<UUID> getChildren(UUID itemId) {
        DashboardLayout item = findLayout(getRootLayout(), itemId);
        List<UUID> al = new ArrayList<>();
        if (item instanceof GridLayout) {
            GridLayout gridLayout = (GridLayout) item;
            al.addAll(gridLayout.getAreas().stream().map(GridArea::getComponent).map(DashboardLayout::getId).collect(Collectors.toList()));
        } else {
            al.addAll(item.getChildren().stream().map(DashboardLayout::getId).collect(Collectors.toList()));
        }
        return al;
    }

    @Override
    public UUID getParent(UUID itemId) {
        DashboardLayout item = findLayout(getRootLayout(), itemId);
        DashboardLayout root = getItems().stream().findFirst().orElse(null);
        if (root.getId().equals(item.getId())) {
            return null;
        } else {
            DashboardLayout parent = findParentLayout(root, item);
            return parent == null ? null : parent.getUuid();
        }
    }

    @Override
    public Collection<UUID> getRootItemIds() {
        List<UUID> al = new ArrayList<>(1);
        al.add(getItems().stream().findFirst().map(DashboardLayout::getId).orElse(null));
        return al;
    }

    @Override
    public boolean isRoot(UUID itemId) {
        UUID root = getItems().stream().findFirst().map(DashboardLayout::getId).orElse(null);
        if (itemId.equals(root)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean hasChildren(UUID itemId) {
        DashboardLayout item = findLayout(getRootLayout(), itemId);
        if (item instanceof GridLayout) {
            GridLayout gridLayout = (GridLayout) item;
            return gridLayout.getAreas().size() > 0;
        } else {
            return item.getChildren().size() > 0;
        }
    }

    private DashboardLayout getRootLayout() {
        return getItems().stream().findFirst().orElse(null);
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
