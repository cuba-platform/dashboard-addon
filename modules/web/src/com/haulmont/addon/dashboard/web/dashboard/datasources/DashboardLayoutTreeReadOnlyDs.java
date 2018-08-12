package com.haulmont.addon.dashboard.web.dashboard.datasources;

import com.haulmont.addon.dashboard.model.visual_model.*;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.data.impl.CustomHierarchicalDatasource;

import java.util.*;
import java.util.stream.Collectors;

public class DashboardLayoutTreeReadOnlyDs extends CustomHierarchicalDatasource<DashboardLayout, UUID> {

    private VerticalLayout visualModel;

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
            return findParentLayout(root, item);
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

    private UUID findParentLayout(DashboardLayout root, DashboardLayout child) {
        if (root instanceof GridLayout) {
            GridLayout gridLayout = (GridLayout) root;
            for (GridArea gridArea : gridLayout.getAreas()) {
                if (gridArea.getComponent().getId().equals(child.getId())) {
                    return root.getId();
                } else {
                    return findParentLayout(gridArea.getComponent(), child);
                }
            }
        } else {
            for (DashboardLayout dashboardLayout : root.getChildren()) {
                if (dashboardLayout.getId().equals(child.getId())) {
                    return root.getId();
                } else {
                    return findParentLayout(dashboardLayout, child);
                }
            }
        }
        return null;
    }

    private DashboardLayout findLayout(DashboardLayout root, UUID uuid) {
        if (root.getId().equals(uuid)) {
            return root;
        }
        if (root instanceof GridLayout) {
            GridLayout gridLayout = (GridLayout) root;
            for (GridArea gridArea : gridLayout.getAreas()) {
                if (gridArea.getComponent().getId().equals(uuid)) {
                    return gridArea.getComponent();
                } else {
                    DashboardLayout tmp = findLayout(gridArea.getComponent(), uuid);
                    if (tmp == null) {
                        continue;
                    }
                    return findLayout(gridArea.getComponent(), uuid);
                }
            }
        } else {
            for (DashboardLayout dashboardLayout : root.getChildren()) {
                if (dashboardLayout.getId().equals(uuid)) {
                    return dashboardLayout;
                } else {
                    DashboardLayout tmp = findLayout(dashboardLayout, uuid);
                    if (tmp == null) {
                        continue;
                    }
                    return findLayout(dashboardLayout, uuid);
                }
            }
        }
        return null;
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

    public void setVisualModel(VerticalLayout visualModel) {
        this.visualModel = visualModel;
    }
}
