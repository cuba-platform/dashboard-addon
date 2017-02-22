/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.utils;

import com.audimex.dashboard.web.widgets.GridCell;
import com.audimex.dashboard.web.widgets.GridRow;
import com.audimex.dashboard.web.widgets.WidgetPanel;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Tree;
import fi.jasoft.dragdroplayouts.DDGridLayout;
import fi.jasoft.dragdroplayouts.DDHorizontalLayout;
import fi.jasoft.dragdroplayouts.DDVerticalLayout;

import java.util.Collection;

public class TreeUtils {
    public static void addComponent(Tree tree, Component parentId, Component component, int position) {
        tree.addItem(component);
        tree.setItemCaption(component, getTreeItemCaption(component));
        HierarchicalContainer container = (HierarchicalContainer) tree.getContainerDataSource();

        if (component instanceof GridLayout) {
            for (int i=0; i<((GridLayout) component).getRows(); i++) {
                LayoutUtils.createNewGridRow((GridLayout) component, tree, i);
            }
            tree.expandItem(component);
        } else {
            tree.setChildrenAllowed(component, false);
        }

        if (parentId != null) {
            tree.setChildrenAllowed(parentId, true);
            tree.setParent(component, parentId);
            tree.expandItem(parentId);
        }

        moveToIndex(container, component, container.getChildren(parentId), position);
    }

    public static void moveComponent(Tree tree, Object parentId, Object component, int position) {
        Object oldParentId = tree.getParent(component);
        HierarchicalContainer container = (HierarchicalContainer) tree.getContainerDataSource();
        if (parentId != null) {
            tree.setChildrenAllowed(parentId, true);
            tree.setParent(component, parentId);
            tree.expandItem(parentId);
        }

        if (tree.getChildren(oldParentId) == null) {
            tree.setChildrenAllowed(oldParentId, false);
        }

        moveToIndex(container, component, container.getChildren(parentId), position);
    }

    private static void moveToIndex(HierarchicalContainer container, Object component, Collection<?> components, int position) {
        if (position > 0) {
            int index = 0;
            for (Object childItemId : components) {
                if (index == position - 1) {
                    container.moveAfterSibling(component, childItemId);
                    break;
                }
                index++;
            }
        } else {
            container.moveAfterSibling(component, null);
        }
    }

    public synchronized static boolean removeComponent(Tree tree, Object itemId) {
        Object parent = tree.getParent(itemId);
        HierarchicalContainer container = (HierarchicalContainer) tree.getContainerDataSource();
        if (parent != null) {
            container.removeItemRecursively(itemId);
            if (tree.getChildren(parent) == null) {
                tree.setChildrenAllowed(parent, false);
            }
            return true;
        } else {
            return false;
        }
    }

    public static int calculatePosition(Tree tree, Object itemId) {
        int index = 0;
        for (Object item : tree.getChildren(tree.getParent(itemId))) {
            if (item == itemId) {
                return index;
            }
            index++;
        }
        return 0;
    }

    public static String getTreeItemCaption(Object component) {
        if (component instanceof WidgetPanel) {
            return "Widget";
        } else if (component instanceof GridCell) {
            return "Cell";
        } else if (component instanceof DDVerticalLayout) {
            return "Vertical";
        } else if (component instanceof DDHorizontalLayout) {
            return "Horizontal";
        } else if (component instanceof DDGridLayout) {
            return "Grid";
        } else if (component instanceof GridRow) {
            return "Row";
        } else {
            return "Undefined";
        }
    }

    public static void reorder(Tree tree, Component parent, Component component, int position) {
        Object parentId = tree.getParent(component);
        tree.removeItem(component);
        if (tree.getChildren(parentId) == null) {
            tree.setChildrenAllowed(parentId, false);
        }
        addComponent(tree, parent, component, position);
    }

    public static void redrawLayout(Tree tree, DDVerticalLayout rootDashboardPanel) {
        rootDashboardPanel.removeAllComponents();
        Collection treeChildren = tree.getChildren(rootDashboardPanel);
        if (treeChildren != null) {
            drawComponents(tree, rootDashboardPanel, treeChildren);
        }
    }

    private static void drawComponents(Tree tree, Object layout, Collection chlidren) {
        for (Object component : chlidren) {
            if (component instanceof GridCell) {
                GridLayout grid = ((GridRow) layout).getGridLayout();
                GridCell gridCell = (GridCell) component;
                DashboardUtils.removeEmptyLabelsForSpan(grid, gridCell);
                grid.removeComponent(
                        ((GridCell) component).getColumn(),
                        ((GridCell) component).getRow()
                );
                Component cellComponent = getGridCellComponent(tree, gridCell);
                if (cellComponent instanceof GridCell) {
                    ((GridCell) cellComponent).setColspan(1);
                    ((GridCell) cellComponent).setRowspan(1);
                    Component gridCellComponent = grid.getComponent(gridCell.getColumn(), gridCell.getRow());
                    if (gridCellComponent == null) {
                        grid.addComponent(
                                cellComponent,
                                gridCell.getColumn(),
                                gridCell.getRow(),
                                gridCell.getColumn()+gridCell.getColspan()-1,
                                gridCell.getRow()+gridCell.getRowspan()-1
                        );
                    }
                } else {
                    if (cellComponent instanceof WidgetPanel) {
                        ((WidgetPanel) cellComponent).setColSpan(gridCell.getColspan());
                        ((WidgetPanel) cellComponent).setRowSpan(gridCell.getRowspan());
                    }
                    grid.addComponent(
                            cellComponent,
                            gridCell.getColumn(),
                            gridCell.getRow(),
                            gridCell.getColumn()+gridCell.getColspan()-1,
                            gridCell.getRow()+gridCell.getRowspan()-1
                    );
                }

            } else if (!(component instanceof GridRow)) {
                ((AbstractOrderedLayout) layout).addComponent((Component) component);
            }

            Collection childCollection = null;
            if (component instanceof GridCell) {
                Collection<?> cellChildren = tree.getChildren(component);
                if (cellChildren != null) {
                    childCollection = tree.getChildren(cellChildren.iterator().next());
                }
            } else {
                childCollection = tree.getChildren(component);
            }

            if (childCollection != null) {
                drawComponents(tree, component, childCollection);
            }
        }
    }

    private static Component getGridCellComponent(Tree tree, GridCell gridCell) {
        Collection<?> children = tree.getChildren(gridCell);
        if (children != null) {
            return (Component) tree.getChildren(gridCell).iterator().next();
        } else {
            return gridCell;
        }
    }

    public static void markGridCells(Tree tree, GridLayout gridLayout,
                                     int row, int column, int rowspan, int colspan) {
        Collection<?> rows = tree.getChildren(gridLayout);
        for (Object gridRow : rows) {
            Collection<?> cells = tree.getChildren(gridRow);
            for (Object gridCell : cells) {
                GridCell currentCell = (GridCell) gridCell;
                if (currentCell.getRow() >= row
                        && currentCell.getRow() < row+rowspan
                        && currentCell.getColumn() >= column
                        && currentCell.getColumn() < column+colspan) {
                    currentCell.setAvailable(false);
                } else {
                    currentCell.setAvailable(true);
                }
            }
        }

        tree.markAsDirty();
    }
}
