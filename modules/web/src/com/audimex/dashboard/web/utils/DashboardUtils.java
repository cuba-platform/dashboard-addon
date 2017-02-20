/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.utils;

import com.audimex.dashboard.entity.ComponentType;
import com.audimex.dashboard.web.widgets.GridCell;
import com.audimex.dashboard.web.widgets.GridRow;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Tree;
import fi.jasoft.dragdroplayouts.DDGridLayout;
import fi.jasoft.dragdroplayouts.DDHorizontalLayout;
import fi.jasoft.dragdroplayouts.DDVerticalLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;

public class DashboardUtils {

    public static BiMap<Resource, String> iconNames = ImmutableBiMap.of(
        FontAwesome.ROCKET, "ROCKET",
        FontAwesome.MAGIC, "MAGIC",
        FontAwesome.DOLLAR, "DOLLAR",
        FontAwesome.BUILDING, "BUILDING",
        FontAwesome.BOOKMARK_O, "BOOKMARK_O"
    );

    private DashboardUtils() {
    }

    public static ComponentType getTypeByComponent(Component component) {
        ComponentType componentType;
        if (component instanceof DDVerticalLayout) {
            componentType = ComponentType.VERTICAL_LAYOUT;
        } else if (component instanceof DDHorizontalLayout) {
            componentType = ComponentType.HORIZONTAL_LAYOUT;
        } else if (component instanceof DDGridLayout) {
            componentType = ComponentType.GRID_LAYOUT;
        } else {
            componentType = ComponentType.WIDGET;
        }
        
        return componentType;
    }

    public static GridLayout removeEmptyLabels(GridLayout gridLayout) {
        for (int i=0; i<gridLayout.getRows(); i++) {
            for (int j=0; j<gridLayout.getColumns(); j++) {
                Component innerComponent = gridLayout.getComponent(j, i);
                if (innerComponent != null && innerComponent instanceof Label) {
                    gridLayout.removeComponent(j, i);
                }
            }
        }

        return gridLayout;
    }

    public static GridLayout addEmptyLabels(GridLayout gridLayout, Tree tree) {
        for (int i=0; i<gridLayout.getRows(); i++) {
            for (int j=0; j<gridLayout.getColumns(); j++) {
                GridRow gridRow = LayoutUtils.getGridRow(i, tree, gridLayout);
                Component innerComponent = gridLayout.getComponent(j, i);
                if (innerComponent == null) {
                    GridCell gridCell = LayoutUtils.createGridCell(j, i, tree);
                    gridLayout.addComponent(gridCell, j, i);
                    tree.addItem(gridCell);
                    tree.setItemCaption(gridCell, TreeUtils.getTreeItemCaption(gridCell));
                    tree.setChildrenAllowed(gridCell, false);
                    tree.setParent(gridCell, gridRow);
                }
            }
        }

        return gridLayout;
    }

    public static LayoutDragMode getDefaultDragMode() {
        return LayoutDragMode.CLONE;
    }
}