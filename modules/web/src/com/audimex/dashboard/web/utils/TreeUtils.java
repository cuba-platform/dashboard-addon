/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.utils;

import com.audimex.dashboard.web.drophandlers.DDVerticalLayoutDropHandler;
import com.audimex.dashboard.web.widgets.GridCell;
import com.audimex.dashboard.web.widgets.GridRow;
import com.audimex.dashboard.web.widgets.WidgetPanel;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.*;
import fi.jasoft.dragdroplayouts.DDGridLayout;
import fi.jasoft.dragdroplayouts.DDHorizontalLayout;
import fi.jasoft.dragdroplayouts.DDVerticalLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;

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
        tree.removeItem(component);
        Object parentId = tree.getParent(component);
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
                grid.removeComponent(
                        ((GridCell) component).getColumn(),
                        ((GridCell) component).getRow()
                );
                grid.addComponent(
                        getGridCellComponent(tree, ((GridCell) component)),
                        ((GridCell) component).getColumn(),
                        ((GridCell) component).getRow()
                );
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
}
