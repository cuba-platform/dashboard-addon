/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.utils;

import com.audimex.dashboard.web.drophandlers.DDGridLayoutDropHandler;
import com.audimex.dashboard.web.drophandlers.DDHorizontalLayoutDropHandler;
import com.audimex.dashboard.web.drophandlers.DDVerticalLayoutDropHandler;
import com.audimex.dashboard.web.drophandlers.GridDropListener;
import com.audimex.dashboard.web.widgets.GridCell;
import com.audimex.dashboard.web.widgets.GridRow;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Tree;
import fi.jasoft.dragdroplayouts.DDGridLayout;
import fi.jasoft.dragdroplayouts.DDHorizontalLayout;
import fi.jasoft.dragdroplayouts.DDVerticalLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;

import java.util.Collection;

public class LayoutUtils {
    public static Component createHorizontalDropLayout(Tree tree, GridDropListener gridDropListener) {
        DDHorizontalLayout component = new DDHorizontalLayout();
        component.setDragMode(DashboardUtils.getDefaultDragMode());

        DDHorizontalLayoutDropHandler ddHorizontalLayoutDropHandler =
                new DDHorizontalLayoutDropHandler();
        ddHorizontalLayoutDropHandler.setComponentDescriptorTree(tree);
        ddHorizontalLayoutDropHandler.setGridDropListener(gridDropListener);
        component.setDropHandler(ddHorizontalLayoutDropHandler);

        configLayout(component);

        return component;
    }

    public static Component createGridDropLayout(Tree tree, GridDropListener gridDropListener) {
        DDGridLayout component = new DDGridLayout();
        component.setDragMode(DashboardUtils.getDefaultDragMode());

        DDGridLayoutDropHandler ddGridLayoutDropHandler = new DDGridLayoutDropHandler();
        ddGridLayoutDropHandler.setComponentDescriptorTree(tree);
        ddGridLayoutDropHandler.setGridDropListener(gridDropListener);
        component.setDropHandler(ddGridLayoutDropHandler);

        component.setSizeFull();
        component.setStyleName("dd-bordering");
        component.setSpacing(true);
        component.setMargin(true);

        component.setColumns(2);
        component.setRows(2);

        return component;
    }

    public static Component createVerticalDropLayout(Tree tree, GridDropListener gridDropListener) {
        DDVerticalLayout component = new DDVerticalLayout();
        component.setDragMode(DashboardUtils.getDefaultDragMode());

        DDVerticalLayoutDropHandler ddVerticalLayoutDropHandler = new DDVerticalLayoutDropHandler();
        ddVerticalLayoutDropHandler.setGridDropListener(gridDropListener);
        ddVerticalLayoutDropHandler.setComponentDescriptorTree(tree);
        component.setDropHandler(ddVerticalLayoutDropHandler);

        configLayout(component);

        return component;
    }

    public static GridCell createGridCell(int column, int row, Tree tree) {
        GridCell gridCell = new GridCell(column, row);
        gridCell.setSizeFull();
        gridCell.setStyleName("dd-bordering");
        gridCell.setSpacing(true);
        gridCell.setMargin(true);
        gridCell.setDragMode(LayoutDragMode.CLONE);
        DDVerticalLayoutDropHandler dropHandler = new DDVerticalLayoutDropHandler();
        dropHandler.setComponentDescriptorTree(tree);
        gridCell.setDropHandler(dropHandler);
        return gridCell;
    }

    public static GridRow getGridRow(int rowIndex, Tree tree, GridLayout gridLayout) {
        Collection<?> gridRows = tree.getChildren(gridLayout);
        if (rowIndex < gridLayout.getRows()) {
            int i=0;
            for (Object row : gridRows) {
                if (i == rowIndex) {
                    return (GridRow) row;
                }
                i++;
            }
            return createNewGridRow(gridLayout, tree, rowIndex);
        } else {
            return createNewGridRow(gridLayout, tree, rowIndex);
        }
    }

    public static GridRow createNewGridRow(GridLayout component, Tree tree, int row) {
        GridRow gridRow = new GridRow(component);
        tree.addItem(gridRow);
        tree.setItemCaption(gridRow, TreeUtils.getTreeItemCaption(gridRow));
        tree.expandItem(gridRow);
        tree.setParent(gridRow, component);

        for (int column = 0; column < component.getColumns(); column++) {
            GridCell gridCell = LayoutUtils.createGridCell(column, row, tree);
            gridRow.addGridCell(gridCell);

            component.addComponent(gridCell, column, row);
            tree.addItem(gridCell);
            tree.setItemCaption(gridCell, TreeUtils.getTreeItemCaption(gridCell));
            tree.expandItem(gridCell);
            tree.setChildrenAllowed(gridCell, false);
            tree.setParent(gridCell, gridRow);
        }

        return gridRow;
    }

    private static Component configLayout(AbstractOrderedLayout component) {
        component.setSizeFull();
        component.setStyleName("dd-bordering");
        component.setSpacing(true);
        component.setMargin(true);

        return component;
    }
}
