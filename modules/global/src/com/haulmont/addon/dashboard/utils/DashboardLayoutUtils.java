/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.utils;

import com.haulmont.addon.dashboard.model.visualmodel.DashboardLayout;
import com.haulmont.addon.dashboard.model.visualmodel.GridArea;
import com.haulmont.addon.dashboard.model.visualmodel.GridLayout;

import java.util.UUID;

public class DashboardLayoutUtils {

    public static DashboardLayout findParentLayout(DashboardLayout root, DashboardLayout child) {
        return findParentLayout(root, child.getId());
    }

    public static DashboardLayout findParentLayout(DashboardLayout root, UUID childId) {
        if (root instanceof GridLayout) {
            GridLayout gridLayout = (GridLayout) root;
            for (GridArea gridArea : gridLayout.getAreas()) {
                if (gridArea.getComponent().getId().equals(childId)) {
                    return root;
                } else {
                    DashboardLayout tmp = findParentLayout(gridArea.getComponent(), childId);
                    if (tmp == null) {
                        continue;
                    }
                    return findParentLayout(gridArea.getComponent(), childId);
                }
            }
        } else {
            for (DashboardLayout dashboardLayout : root.getChildren()) {
                if (dashboardLayout.getId().equals(childId)) {
                    return root;
                } else {
                    DashboardLayout tmp = findParentLayout(dashboardLayout, childId);
                    if (tmp == null) {
                        continue;
                    }
                    return findParentLayout(dashboardLayout, childId);
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
        } catch (Exception ignored) {
        }
        return null;
    }
}
