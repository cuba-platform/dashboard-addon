/*
 * Copyright (c) 2008-2018 Haulmont.
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
 *
 */

package com.haulmont.addon.dashboard.utils;

import com.haulmont.addon.dashboard.model.visualmodel.*;

import java.util.ArrayList;
import java.util.List;
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

    public static boolean isLinearLayout(DashboardLayout layout) {
        return layout instanceof HorizontalLayout || layout instanceof VerticalLayout;
    }

    public static boolean isParentCssLayout(DashboardLayout layout) {
        return layout.getParent() instanceof CssLayout;
    }

    public static boolean isParentHasExpand(DashboardLayout layout) {
        DashboardLayout parent = layout.getParent();
        if (isLinearLayout(parent) && parent.getExpand() != null) {
            return parent.getChildren().stream()
                    .anyMatch(e -> e.getId().equals(parent.getExpand()));
        }
        return false;
    }

    public static boolean isGridCellLayout(DashboardLayout layout) {
        return (layout.getParent() instanceof GridLayout);
    }

    public static boolean isRootLayout(DashboardLayout layout) {
        return (layout instanceof RootLayout);
    }

    public static List<DashboardLayout> findParentsLayout(DashboardLayout root, UUID child) {
        List<DashboardLayout> result = new ArrayList<>();
        DashboardLayout layout;
        UUID ch = child;
        while ((layout = findParentLayout(root, ch)) != null) {
            result.add(layout);
            ch = layout.getUuid();
        }
        return result;
    }
}
