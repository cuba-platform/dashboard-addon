package com.audimex.dashboard.web.tools;

import com.audimex.dashboard.entity.DashboardWidget;
import com.audimex.dashboard.web.drophandlers.GridDropListener;
import com.audimex.dashboard.web.layouts.*;
import com.audimex.dashboard.web.widgets.FramePanel;
import com.audimex.dashboard.web.widgets.GridCell;
import com.audimex.dashboard.web.widgets.GridRow;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.reports.entity.Report;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Tree;
import fi.jasoft.dragdroplayouts.interfaces.DragGrabFilterSupport;
import fi.jasoft.dragdroplayouts.interfaces.HasDragCaptionProvider;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Component(DashboardTools.NAME)
public class DashboardTools {
    public static final String NAME = "amxd_DashboardTools";

    public static final String AMXD_DASHBOARD_DISABLED = "amxd-dashboard-disabled";
    public static final String AMXD_CONTAINER_DISABLED = "amxd-container-disabled";
    public static final String AMXD_TREE_SELECTED = "amxd-tree-selected";
    public static final String AMXD_SHADOW_BORDER = "amxd-shadow-border";
    public static final String AMXD_GRID_CELL_SHADOW_BORDER = "amxd-grid-cell-shadow-border";
    public static final String AMXD_LAYOUT_CONTROLS = "amxd-layout-controls";
    public static final String AMXD_MAIN_LAYOUT_CONTROLS_SHOW = "amxd-main-layout-controls-show";
    public static final String AMXD_WIDGETS_LAYOUT_CONTROLS_SHOW = "amxd-widgets-layout-controls-show";
    public static final String AMXD_DASHBOARD_BUTTON = "amxd-dashboard-button";
    public static final String AMXD_NOT_AVAILABLE = "amxd-not-available";
    public static final String AMXD_EDIT_BUTTON = "amxd-edit-button";
    public static final String AMXD_VIEW_BUTTON = "amxd-view-button";

    public static final String COMMON = "COMMON";
    public static final String LIST = "LIST";
    public static final String CHART = "CHART";

    public DashboardGridLayout addEmptyLabels(DashboardGridLayout gridLayout, Tree tree) {
        for (int i = 0; i < gridLayout.getRows(); i++) {
            for (int j = 0; j < gridLayout.getColumns(); j++) {
                GridRow gridRow = getGridRow(i, tree, gridLayout);
                com.vaadin.ui.Component innerComponent = gridLayout.getComponent(j, i);

                GridCell gridCell = getGridCell(tree, tree.getChildren(gridLayout), j, i);
                if (gridCell == null) {
                    gridCell = createGridCell(j, i);
                }
                if (innerComponent == null) {
                    gridLayout.addComponent(gridCell, j, i);
                }
                if (!(innerComponent instanceof GridCell)) {
                    tree.addItem(gridCell);
                    tree.setItemCaption(gridCell, getTreeItemCaption(gridCell));
                    tree.setChildrenAllowed(gridCell, false);
                    tree.setParent(gridCell, gridRow);
                    if (innerComponent != null) {
                        tree.setParent(innerComponent, gridCell);
                        tree.setChildrenAllowed(gridCell, true);
                    }
                }
            }
        }

        return gridLayout;
    }

    public void lockGridCells(DashboardGridLayout gridLayout, Tree tree) {
        for (int i = 0; i < gridLayout.getRows(); i++) {
            for (int j = 0; j < gridLayout.getColumns(); j++) {
                GridRow gridRow = getGridRow(i, tree, gridLayout);
                com.vaadin.ui.Component innerComponent = gridLayout.getComponent(j, i);

                GridCell gridCell = getGridCell(tree, tree.getChildren(gridLayout), j, i);
                com.vaadin.ui.Component gridChild = gridLayout.getComponent(j, i);

                if (gridChild instanceof GridCell) {
                    gridCell.setAvailable(true);
                } else {
                    gridCell.setAvailable(false);
                }
            }
        }
    }

    public void removeEmptyLabelsForSpan(DashboardGridLayout gridLayout, GridCell gridCell) {
        for (int i = gridCell.getRow(); i < gridCell.getRow() + gridCell.getRowspan(); i++) {
            for (int j = gridCell.getColumn(); j < gridCell.getColumn() + gridCell.getColspan(); j++) {
                com.vaadin.ui.Component gridChild = gridLayout.getComponent(j, i);
                if (gridChild instanceof GridCell) {
                    gridLayout.removeComponent(j, i);
                }
            }
        }
    }

    public com.vaadin.ui.Component createGridDropLayout(Tree tree,  GridDropListener gridDropListener,
                                                               Frame frame, Consumer<Tree> treeHandler) {
        DashboardGridLayout component = new DashboardGridLayout(tree, gridDropListener, frame, treeHandler);
        configLayout(component);
        component.setColumns(2);
        component.setRows(2);

        return component;
    }

    public com.vaadin.ui.Component createHorizontalDropLayout(Tree tree, GridDropListener gridDropListener,
                                                                     Frame frame, Consumer<Tree> treeHandler) {
        DashboardHorizontalLayout component =
                new DashboardHorizontalLayout(tree, gridDropListener, frame, treeHandler);
        configLayout(component);
        return component;
    }

    public com.vaadin.ui.Component createVerticalDropLayout(Tree tree, GridDropListener gridDropListener,
                                                                   Frame frame, Consumer<Tree> treeHandler) {
        DashboardVerticalLayout component =
                new DashboardVerticalLayout(tree, gridDropListener, frame, treeHandler);
        configLayout(component);
        return component;
    }

    protected com.vaadin.ui.Component configLayout(AbstractLayout component) {
        component.setSizeFull();
        component.setStyleName(AMXD_SHADOW_BORDER);

        return component;
    }

    public GridCell createGridCell(int column, int row) {
        GridCell gridCell = new GridCell(column, row);
        gridCell.setSizeFull();
        gridCell.setStyleName(AMXD_GRID_CELL_SHADOW_BORDER);

        return gridCell;
    }

    public GridRow getGridRow(int rowIndex, Tree tree, DashboardGridLayout gridLayout) {
        Collection<?> gridRows = tree.getChildren(gridLayout);
        if (rowIndex < gridLayout.getRows()) {
            int i = 0;
            if (gridRows != null) {
                for (Object row : gridRows) {
                    if (i == rowIndex) {
                        return (GridRow) row;
                    }
                    i++;
                }
            }
            return createNewGridRow(gridLayout, tree, rowIndex);
        } else {
            return createNewGridRow(gridLayout, tree, rowIndex);
        }
    }

    public GridRow createNewGridRow(DashboardGridLayout component, Tree tree, int row) {
        GridRow gridRow = new GridRow(component);
        tree.addItem(gridRow);
        tree.setItemCaption(gridRow, getTreeItemCaption(gridRow));
        tree.expandItem(gridRow);
        tree.setParent(gridRow, component);

        for (int column = 0; column < component.getColumns(); column++) {
            GridCell gridCell = getGridCell(tree, tree.getChildren(component), column, row);
            if (gridCell == null) {
                gridCell = createGridCell(column, row);
            }

            gridRow.addGridCell(gridCell);

            if (component.getComponent(column, row) != null) {
                component.removeComponent(column, row);
            }

            component.addComponent(gridCell, column, row);
            tree.addItem(gridCell);
            tree.setItemCaption(gridCell, getTreeItemCaption(gridCell));
            tree.expandItem(gridCell);
            tree.setChildrenAllowed(gridCell, false);
            tree.setParent(gridCell, gridRow);
        }

        return gridRow;
    }

    public GridCell getGridCell(Tree tree, Collection<?> rows, int columnIndex, int rowIndex) {
        if (rows != null) {
            for (Object row : rows) {
                Collection<?> cells = tree.getChildren(row);
                if (cells == null) {
                    return null;
                }
                for (Object cell : cells) {
                    GridCell gridCell = (GridCell) cell;
                    if (gridCell.getColumn() == columnIndex && gridCell.getRow() == rowIndex) {
                        return gridCell;
                    }
                }
            }
        }
        return null;
    }

    public int availableColumns(GridLayout gridLayout, GridCell gridCell) {
        int availableColumns = gridCell.getColspan();
        for (int column = gridCell.getColumn() + gridCell.getColspan(); column < gridLayout.getColumns(); column++) {
            com.vaadin.ui.Component gridLayoutComponent = gridLayout.getComponent(column, gridCell.getRow());
            if (!(gridLayoutComponent instanceof GridCell)) {
                break;
            }
            availableColumns++;
        }
        return availableColumns;
    }

    public int availableRows(GridLayout gridLayout, GridCell gridCell) {
        int availableRows = gridCell.getRowspan();
        for (int row = gridCell.getRow() + gridCell.getRowspan(); row < gridLayout.getRows(); row++) {
            com.vaadin.ui.Component gridLayoutComponent = gridLayout.getComponent(gridCell.getColumn(), row);
            if (!(gridLayoutComponent instanceof GridCell)) {
                break;
            }
            availableRows++;
        }
        return availableRows;
    }

    public GridCell getWidgetCell(Tree tree, com.vaadin.ui.Component widget) {
        return (GridCell) tree.getParent(widget);
    }

    public void addComponent(Tree tree, Object parentId, Object component, int position) {
        tree.addItem(component);
        tree.setItemCaption(component, getTreeItemCaption(component));
        HierarchicalContainer container = (HierarchicalContainer) tree.getContainerDataSource();

        if (component instanceof DashboardGridLayout) {
            for (int i = 0; i < ((DashboardGridLayout) component).getRows(); i++) {
                createNewGridRow((DashboardGridLayout) component, tree, i);
            }
            tree.expandItem(component);
        } else {
            tree.setChildrenAllowed(component, false);
        }

        if (parentId != null) {
            if (parentId instanceof DragGrabFilterSupport && component instanceof DragGrabFilterSupport) {
                ((DragGrabFilterSupport) component).setDragGrabFilter(
                        ((DragGrabFilterSupport) parentId).getDragGrabFilter()
                );
            }
            if (parentId instanceof HasDragCaptionProvider && component instanceof HasDragCaptionProvider) {
                ((HasDragCaptionProvider) component).setDragCaptionProvider(
                        ((HasDragCaptionProvider) parentId).getDragCaptionProvider()
                );
            }

            tree.setChildrenAllowed(parentId, true);
            tree.setParent(component, parentId);
            tree.expandItem(parentId);
        }

        moveToIndex(container, component, container.getChildren(parentId), position);
        tree.setValue(component);
        tree.focus();
    }

    public String getTreeItemCaption(Object component) {
        Messages messages = AppBeans.get(Messages.NAME);
        if (component instanceof FramePanel) {
            String caption = ((FramePanel) component).getWidgetCaption();
            if (caption != null) {
                return messages.getMainMessage(caption);
            } else {
                return messages.getMainMessage("dashboard.widget");
            }
        } else if (component instanceof GridCell) {
            return messages.getMainMessage("dashboard.cell");
        } else if (component instanceof DashboardVerticalLayout) {
            return messages.getMainMessage("dashboard.vertical");
        } else if (component instanceof DashboardHorizontalLayout) {
            return messages.getMainMessage("dashboard.horizontal");
        } else if (component instanceof DashboardGridLayout) {
            return messages.getMainMessage("dashboard.grid");
        } else if (component instanceof GridRow) {
            return messages.getMainMessage("dashboard.row");
        } else {
            return messages.getMainMessage("dashboard.undefined");
        }
    }

    public void moveComponent(Tree tree, Object parentId, Object component, int position) {
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

    protected void moveToIndex(HierarchicalContainer container, Object component, Collection<?> components, int position) {
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

    public boolean removeComponent(Tree tree, Object itemId) {
        if (itemId instanceof GridCell || itemId instanceof GridRow) {
            return false;
        }

        Object parent = tree.getParent(itemId);
        HierarchicalContainer container = (HierarchicalContainer) tree.getContainerDataSource();
        if (parent != null) {
            container.removeItemRecursively(itemId);
            if (tree.getChildren(parent) == null) {
                tree.setChildrenAllowed(parent, false);
            }
            com.vaadin.ui.Component component = (com.vaadin.ui.Component) itemId;

            if (component.getParent() != null) {
                AbstractLayout parentComponent = (AbstractLayout) component.getParent();
                parentComponent.removeComponent(component);
            }
            return true;
        } else {
            return false;
        }
    }

    public int calculatePosition(Tree tree, Object itemId) {
        int index = 0;
        for (Object item : tree.getChildren(tree.getParent(itemId))) {
            if (item == itemId) {
                return index;
            }
            index++;
        }
        return 0;
    }

    public void reorder(Tree tree, com.vaadin.ui.Component parent, com.vaadin.ui.Component component, int position) {
        Object parentId = tree.getParent(component);
        tree.removeItem(component);
        if (tree.getChildren(parentId) == null) {
            tree.setChildrenAllowed(parentId, false);
        }
        addComponent(tree, parent, component, position);
    }

    public void redrawLayout(Tree tree, DashboardVerticalLayout rootDashboardPanel) {
        rootDashboardPanel.getMainLayout().removeAllComponents();
        Collection treeChildren = tree.getChildren(rootDashboardPanel);
        if (treeChildren != null) {
            drawComponents(tree, rootDashboardPanel, treeChildren);
        }
    }

    protected void drawComponents(Tree tree, Object layout, Collection chlidren) {
        for (Object component : chlidren) {
            if (component instanceof GridCell) {
                DashboardGridLayout grid = ((GridRow) layout).getGridLayout();
                GridCell gridCell = (GridCell) component;

                removeEmptyLabelsForSpan(grid, gridCell);
                grid.removeComponent(
                        ((GridCell) component).getColumn(),
                        ((GridCell) component).getRow()
                );

                Collection<?> children = tree.getChildren(gridCell);

                com.vaadin.ui.Component cellComponent = null;
                if (children != null) {
                    cellComponent = (com.vaadin.ui.Component) tree.getChildren(gridCell).iterator().next();
                } else {
                    cellComponent = gridCell;
                }

                if (cellComponent instanceof GridCell) {
                    ((GridCell) cellComponent).setColspan(1);
                    ((GridCell) cellComponent).setRowspan(1);

                    com.vaadin.ui.Component gridCellComponent = grid.getComponent(gridCell.getColumn(), gridCell.getRow());
                    if (gridCellComponent == null) {
                        grid.addComponent(
                                cellComponent,
                                gridCell.getColumn(),
                                gridCell.getRow(),
                                gridCell.getColumn() + gridCell.getColspan() - 1,
                                gridCell.getRow() + gridCell.getRowspan() - 1
                        );
                    }
                } else {
                    if (cellComponent instanceof FramePanel) {
                        ((FramePanel) cellComponent).setColSpan(gridCell.getColspan());
                        ((FramePanel) cellComponent).setRowSpan(gridCell.getRowspan());
                    }

                    if (grid.getComponent(gridCell.getColumn(), gridCell.getRow()) != null) {
                        grid.removeComponent(gridCell.getColumn(), gridCell.getRow());
                    }

                    grid.addComponent(
                            cellComponent,
                            gridCell.getColumn(),
                            gridCell.getRow(),
                            gridCell.getColumn() + gridCell.getColspan() - 1,
                            gridCell.getRow() + gridCell.getRowspan() - 1
                    );
                }
            } else if (!(component instanceof GridRow)) {
                ((AbstractLayout) layout).addComponent((com.vaadin.ui.Component) component);

                if (component instanceof HasMainLayout
                        && !(component instanceof FramePanel)) {
                    ((HasMainLayout) component).getMainLayout().removeAllComponents();
                }

                if (component instanceof HasWeight) {
                    ((HasWeight) component).setWeight(((HasWeight) component).getWeight());
                } else if (component instanceof GridLayout) {
                    if (((GridLayout) component).getParent() instanceof AbstractOrderedLayout) {
                        ((AbstractOrderedLayout) ((GridLayout) component).getParent())
                                .setExpandRatio((com.vaadin.ui.Component) component, 1);
                    }
                }
            }

            Collection childCollection = null;
            if (component instanceof GridCell) {
                Collection<?> cellChildren = tree.getChildren(component);
                if (cellChildren != null) {
                    component = cellChildren.iterator().next();
                    childCollection = tree.getChildren(component);
                }
            } else {
                childCollection = tree.getChildren(component);
            }

            if (childCollection != null) {
                drawComponents(tree, component, childCollection);
            }
        }
    }

    public void markGridCells(Tree tree, DashboardGridLayout gridLayout,
                                     int row, int column, int rowspan, int colspan) {
        Collection<?> rows = tree.getChildren(gridLayout);
        for (Object gridRow : rows) {
            Collection<?> cells = tree.getChildren(gridRow);
            for (Object gridCell : cells) {
                GridCell currentCell = (GridCell) gridCell;
                if (currentCell.getRow() >= row
                        && currentCell.getRow() < row + rowspan
                        && currentCell.getColumn() >= column
                        && currentCell.getColumn() < column + colspan) {
                    currentCell.setAvailable(false);
                } else {
                    currentCell.setAvailable(true);
                }
            }
        }

        tree.markAsDirty();
    }

    public DashboardWidget getWidget(UUID id) {
        DataManager dataManager = AppBeans.get(DataManager.class);
        LoadContext<DashboardWidget> ctx = LoadContext.create(DashboardWidget.class)
                .setId(id)
                .setView("dashboardWidget-view");
        return dataManager.load(ctx);
    }

    public Report getReport(UUID id) {
        if (id == null) return null;

        DataManager dataManager = AppBeans.get(DataManager.class);
        LoadContext<Report> ctx = LoadContext.create(Report.class)
                .setId(id)
                .setView("report.view");
        return dataManager.load(ctx);
    }

    public Map<String, String> getWidgetViewTypes() {
        Messages messages = AppBeans.get(Messages.NAME);
        return new HashMap<String, String>(){
            { put(messages.getMainMessage("dashboard.widget.common"), COMMON); }
            { put(messages.getMainMessage("dashboard.widget.list"), LIST); }
            { put(messages.getMainMessage("dashboard.widget.chart"), CHART); }
        };
    }
}
