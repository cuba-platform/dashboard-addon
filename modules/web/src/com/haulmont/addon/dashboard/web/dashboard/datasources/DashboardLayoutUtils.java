package com.haulmont.addon.dashboard.web.dashboard.datasources;

import com.haulmont.addon.dashboard.model.visualmodel.DashboardLayout;
import com.haulmont.addon.dashboard.model.visualmodel.GridArea;
import com.haulmont.addon.dashboard.model.visualmodel.GridLayout;

import java.util.UUID;

public class DashboardLayoutUtils {

    public static DashboardLayout findParentLayout(DashboardLayout root, DashboardLayout child) {
        if (root instanceof GridLayout) {
            GridLayout gridLayout = (GridLayout) root;
            for (GridArea gridArea : gridLayout.getAreas()) {
                if (gridArea.getComponent().getId().equals(child.getId())) {
                    return root;
                } else {
                    DashboardLayout tmp = findParentLayout(gridArea.getComponent(), child);
                    if (tmp == null) {
                        continue;
                    }
                    return findParentLayout(gridArea.getComponent(), child);
                }
            }
        } else {
            for (DashboardLayout dashboardLayout : root.getChildren()) {
                if (dashboardLayout.getId().equals(child.getId())) {
                    return root;
                } else {
                    DashboardLayout tmp = findParentLayout(dashboardLayout, child);
                    if (tmp == null) {
                        continue;
                    }
                    return findParentLayout(dashboardLayout, child);
                }
            }
        }
        return null;
    }

    public static DashboardLayout findLayout(DashboardLayout root, UUID uuid) {
        try {
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
        }catch (Exception e){
            int t=5;
        }
        return null;
    }
}
