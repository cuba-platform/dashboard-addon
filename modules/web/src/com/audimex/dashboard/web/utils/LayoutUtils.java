/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.utils;

import com.audimex.dashboard.web.drophandlers.DDGridLayoutDropHandler;
import com.audimex.dashboard.web.drophandlers.GridDropListener;
import com.audimex.dashboard.web.layouts.DashboardHorizontalLayout;
import com.audimex.dashboard.web.layouts.DashboardVerticalLayout;
import com.audimex.dashboard.web.layouts.HasGridSpan;
import com.audimex.dashboard.web.widgets.GridCell;
import com.audimex.dashboard.web.widgets.GridRow;
import com.haulmont.cuba.gui.components.Frame;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Tree;
import fi.jasoft.dragdroplayouts.DDGridLayout;

import java.util.Collection;

public class LayoutUtils {
    public static Component createHorizontalDropLayout(Tree tree, GridDropListener gridDropListener, Frame frame) {
        DashboardHorizontalLayout component = new DashboardHorizontalLayout(tree, gridDropListener, frame);
        configLayout(component);
        return component;
    }

    public static Component createGridDropLayout(Tree tree, GridDropListener gridDropListener, Frame frame) {
        DDGridLayout component = new DDGridLayout();
        component.setDragMode(DashboardUtils.getDefaultDragMode());

        DDGridLayoutDropHandler ddGridLayoutDropHandler = new DDGridLayoutDropHandler();
        ddGridLayoutDropHandler.setTree(tree);
        ddGridLayoutDropHandler.setGridDropListener(gridDropListener);
        ddGridLayoutDropHandler.setFrame(frame);
        component.setDropHandler(ddGridLayoutDropHandler);

        component.setSizeFull();
        component.setStyleName(DashboardUtils.AMXD_BORDERING);
        component.setSpacing(true);
        component.setMargin(true);

        component.setColumns(2);
        component.setRows(2);

        return component;
    }

    public static Component createVerticalDropLayout(Tree tree, GridDropListener gridDropListener, Frame frame) {
        DashboardVerticalLayout component = new DashboardVerticalLayout(tree, gridDropListener, frame);
        configLayout(component);
        return component;
    }

    public static GridCell createGridCell(int column, int row) {
        GridCell gridCell = new GridCell(column, row);
        gridCell.setSizeFull();
        gridCell.setStyleName(DashboardUtils.AMXD_BORDERING);

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
            GridCell gridCell = LayoutUtils.createGridCell(column, row);
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

    protected static Component configLayout(AbstractLayout component) {
        component.setSizeFull();
        component.setStyleName(DashboardUtils.AMXD_BORDERING);

        return component;
    }

    public static GridCell getWidgetCell(Tree tree, Component widget) {
        return (GridCell) tree.getParent(widget);
    }

    public static GridCell getGridCell(Tree tree, Collection<?> rows, int columnIndex, int rowIndex) {
        for (Object row : rows) {
            Collection<?> cells = tree.getChildren(row);
            for (Object cell : cells) {
                GridCell gridCell = (GridCell) cell;
                if (gridCell.getColumn() == columnIndex && gridCell.getRow() == rowIndex) {
                    return gridCell;
                }
            }
        }
        return null;
    }

    public static int availableColumns(GridLayout gridLayout, GridCell gridCell) {
        int availableColumns = gridCell.getColspan();
        for (int column = gridCell.getColumn() + gridCell.getColspan(); column < gridLayout.getColumns(); column++) {
            Component gridLayoutComponent = gridLayout.getComponent(column, gridCell.getRow());
            if (!(gridLayoutComponent instanceof GridCell)) {
                break;
            }
            availableColumns++;
        }
        return availableColumns;
    }

    public static int availableRows(GridLayout gridLayout, GridCell gridCell) {
        int availableRows = gridCell.getRowspan();
        for (int row = gridCell.getRow() + gridCell.getRowspan(); row < gridLayout.getRows(); row++) {
            Component gridLayoutComponent = gridLayout.getComponent(gridCell.getColumn(), row);
            if (!(gridLayoutComponent instanceof GridCell)) {
                break;
            }
            availableRows++;
        }
        return availableRows;
    }

    public static boolean validateSpan(GridLayout gridLayout, Tree tree,
                                       HasGridSpan widgetPanel, int colspan, int rowspan) {
        GridCell gridCell = getWidgetCell(tree, (Component) widgetPanel);
        int availableRowSpan = availableRows(gridLayout, gridCell);
        int availableColSpan = availableColumns(gridLayout, gridCell);

        if (availableRowSpan >= 0
                && availableRowSpan >= rowspan
                && availableColSpan >= colspan
                && availableColSpan >= 0) {
            for (int row = gridCell.getRow(); row < gridCell.getRow() + rowspan; row++) {
                for (int column = gridCell.getColumn(); column < gridCell.getColumn() + colspan; column++) {
                    Component component = gridLayout.getComponent(column, row);
                    if (!(component instanceof GridCell) && !component.equals(widgetPanel)) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
