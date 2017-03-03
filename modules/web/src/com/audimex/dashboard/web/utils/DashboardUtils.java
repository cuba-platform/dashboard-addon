/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.utils;

import com.audimex.dashboard.web.widgets.GridCell;
import com.audimex.dashboard.web.widgets.GridRow;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Tree;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;

public class DashboardUtils {
    public static final String AMXD_DASHBOARD_DISABLED = "amxd-dashboard-disabled";
    public static final String AMXD_CONTAINER_DISABLED = "amxd-container-disabled";
    public static final String AMXD_TREE_SELECTED = "amxd-tree-selected";
    public static final String AMXD_BORDERING = "amxd-bordering";
    public static final String AMXD_LAYOUT_CONTROLS = "amxd-layout-controls";

    protected DashboardUtils() {
    }

    public static GridLayout removeEmptyLabels(GridLayout gridLayout, Tree tree) {
        for (int i = 0; i < gridLayout.getRows(); i++) {
            for (int j = 0; j < gridLayout.getColumns(); j++) {
                Component innerComponent = gridLayout.getComponent(j, i);
                if (innerComponent != null && innerComponent instanceof GridCell) {
                    gridLayout.removeComponent(j, i);
                    tree.removeItem(innerComponent);
                }
            }
        }

        return gridLayout;
    }

    public static GridLayout addEmptyLabels(GridLayout gridLayout, Tree tree) {
        for (int i = 0; i < gridLayout.getRows(); i++) {
            for (int j = 0; j < gridLayout.getColumns(); j++) {
                GridRow gridRow = LayoutUtils.getGridRow(i, tree, gridLayout);
                Component innerComponent = gridLayout.getComponent(j, i);

                GridCell gridCell = LayoutUtils.createGridCell(j, i);
                if (innerComponent == null) {
                    gridLayout.addComponent(gridCell, j, i);
                }
                if (!(innerComponent instanceof GridCell)) {
                    tree.addItem(gridCell);
                    tree.setItemCaption(gridCell, TreeUtils.getTreeItemCaption(gridCell));
                    tree.setChildrenAllowed(gridCell, false);
                    tree.setParent(gridCell, gridRow);
                }
            }
        }

        return gridLayout;
    }

    public static GridLayout addEmptyLabelsToLayout(GridLayout gridLayout, Tree tree) {
        for (int i = 0; i < gridLayout.getRows(); i++) {
            for (int j = 0; j < gridLayout.getColumns(); j++) {
                GridRow gridRow = LayoutUtils.getGridRow(i, tree, gridLayout);
                Component innerComponent = gridLayout.getComponent(j, i);

                GridCell gridCell = LayoutUtils.getGridCell(tree, tree.getChildren(gridLayout), j, i);
                if (innerComponent == null) {
                    gridLayout.addComponent(gridCell, j, i);
                }
            }
        }

        return gridLayout;
    }

    public static void lockGridCells(GridLayout gridLayout, Tree tree) {
        for (int i = 0; i < gridLayout.getRows(); i++) {
            for (int j = 0; j < gridLayout.getColumns(); j++) {
                GridRow gridRow = LayoutUtils.getGridRow(i, tree, gridLayout);
                Component innerComponent = gridLayout.getComponent(j, i);

                GridCell gridCell = LayoutUtils.getGridCell(tree, tree.getChildren(gridLayout), j, i);
                Component gridChild = gridLayout.getComponent(j, i);

                if (gridChild instanceof GridCell) {
                    gridCell.setAvailable(true);
                } else {
                    gridCell.setAvailable(false);
                }
            }
        }
    }

    public static void removeEmptyLabelsForSpan(GridLayout gridLayout, GridCell gridCell) {
        for (int i = gridCell.getRow(); i < gridCell.getRow() + gridCell.getRowspan(); i++) {
            for (int j = gridCell.getColumn(); j < gridCell.getColumn() + gridCell.getColspan(); j++) {
                Component gridChild = gridLayout.getComponent(j, i);
                if (gridChild instanceof GridCell) {
                    gridLayout.removeComponent(j, i);
                }
            }
        }
    }

    public static LayoutDragMode getDefaultDragMode() {
        return LayoutDragMode.CLONE;
    }
}