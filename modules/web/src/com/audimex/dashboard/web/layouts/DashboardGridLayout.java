package com.audimex.dashboard.web.layouts;

import com.audimex.dashboard.web.drophandlers.DDGridLayoutDropHandler;
import com.audimex.dashboard.web.drophandlers.GridDropListener;
import com.audimex.dashboard.web.tools.DashboardTools;
import com.audimex.dashboard.web.widgets.GridCell;
import com.audimex.dashboard.web.widgets.GridRow;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.cuba.web.App;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.Transferable;
import com.vaadin.ui.*;
import fi.jasoft.dragdroplayouts.DDGridLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;
import fi.jasoft.dragdroplayouts.interfaces.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class DashboardGridLayout extends CssLayout implements HasDragCaption, HasWeight, HasGridSpan,
        DragGrabFilterSupport, HasDragCaptionProvider, LayoutDragSource {
    protected String caption;
    protected String icon;
    protected int weight = 1;
    protected int colSpan = 1;
    protected int rowSpan = 1;

    protected Tree tree = null;
    protected DDGridLayout gridLayout = null;

    protected Messages messages = AppBeans.get(Messages.NAME);
    protected DashboardTools dashboardTools;

    public DashboardGridLayout(Tree tree, GridDropListener gridDropListener,
                                     Frame frame, Consumer<Tree> treeHandler) {
        this.tree = tree;
        dashboardTools = AppBeans.get(DashboardTools.NAME);

        HorizontalLayout buttonsPanel = new HorizontalLayout();
        Button configButton = new Button(WebComponentsHelper.getIcon("icons/gear.png"));
        configButton.addStyleName(DashboardTools.AMXD_EDIT_BUTTON);
        configButton.addClickListener(event -> {
            Map<String, Object> params = new HashMap<>();
            params.put("widget", this);
            params.put("tree", tree);

            WindowManager windowManager = App.getInstance().getWindowManager();
            WindowConfig windowConfig = AppBeans.get(WindowConfig.class);
            WindowInfo windowInfo = windowConfig.getWindowInfo("amxd$widgetConfigWindow");
            windowManager.openWindow(windowInfo, WindowManager.OpenType.DIALOG, params);
        });
        Button removeButton = new Button(WebComponentsHelper.getIcon("icons/trash.png"));
        removeButton.addStyleName(DashboardTools.AMXD_EDIT_BUTTON);
        removeButton.addClickListener(event -> {
            Object treeObject = tree.getValue();
            Component treeComponent = null;
            if (treeObject instanceof Component) {
                if (treeObject instanceof GridCell) {
                    Collection<?> gridCellChild = tree.getChildren(treeObject);
                    if (gridCellChild != null) {
                        treeComponent = (Component) gridCellChild.iterator().next();
                    } else {
                        treeComponent = ((GridCell) treeObject).getParent().getParent();
                    }
                } else {
                    treeComponent = (Component) treeObject;
                }
            } else if (treeObject instanceof GridRow) {
                treeComponent = ((GridRow) treeObject).getGridLayout();
            }

            if (treeComponent != null) {
                dashboardTools.removeComponent(tree, treeComponent);
                treeHandler.accept(tree);
            }
        });
        configButton.setDescription(messages.getMainMessage("dashboard.configButton"));
        removeButton.setDescription(messages.getMainMessage("dashboard.removeButton"));
        buttonsPanel.addComponent(configButton);
        buttonsPanel.addComponent(removeButton);
        buttonsPanel.addStyleName(DashboardTools.AMXD_LAYOUT_CONTROLS);

        gridLayout = new DDGridLayout();
        DDGridLayoutDropHandler ddGridLayoutDropHandler = new DDGridLayoutDropHandler();
        ddGridLayoutDropHandler.setTree(tree);
        ddGridLayoutDropHandler.setGridDropListener(gridDropListener);
        ddGridLayoutDropHandler.setFrame(frame);
        ddGridLayoutDropHandler.setTreeHandler(treeHandler);
        gridLayout.setDropHandler(ddGridLayoutDropHandler);

        gridLayout.setDragMode(LayoutDragMode.CLONE);
        gridLayout.setSizeFull();
        gridLayout.setSpacing(true);
        gridLayout.setMargin(true);
        gridLayout.addStyleName("amxd-layout-content");

        super.addComponent(buttonsPanel);
        super.addComponent(gridLayout);
    }

    @Override
    public String getWidgetIcon() {
        return icon;
    }

    public void setWidgetIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String getWidgetCaption() {
        return caption;
    }

    @Override
    public void setWidgetCaption(String caption) {
        this.caption = caption;
    }

    public int getColumns() {
        return gridLayout.getColumns();
    }

    public void setColumns(int cols) {
        gridLayout.setColumns(cols);
    }

    public int getRows() {
        return gridLayout.getRows();
    }

    public void setRows(int rows) {
        gridLayout.setRows(rows);
    }

    public void removeComponent(int column, int row) {
        gridLayout.removeComponent(column, row);
    }

    public Component getComponent(int column, int row) {
        return gridLayout.getComponent(column, row);
    }

    public DDGridLayout getGridLayout() {
        return gridLayout;
    }

    public void addComponent(Component component, int column, int row) {
        gridLayout.addComponent(component, column, row);
    }

    public void addComponent(Component component, int column, int row, int colSpan, int rowSpan) {
        gridLayout.addComponent(component, column, row, colSpan, rowSpan);
    }

    @Override
    public void setWeight(int weight) {
        this.weight = weight;

        if (getParent() instanceof AbstractOrderedLayout) {
            ((AbstractOrderedLayout) getParent()).setExpandRatio(this, weight);
        }
    }

    public void removeComponent(Component component) {
        gridLayout.removeComponent(component);
    }

    @Override
    public void addLayoutClickListener(LayoutEvents.LayoutClickListener listener) {
        gridLayout.addLayoutClickListener(listener);
    }

    @Override
    public void removeLayoutClickListener(LayoutEvents.LayoutClickListener listener) {
        gridLayout.removeLayoutClickListener(listener);
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
        GridCell gridCell = dashboardTools.getWidgetCell(tree, this);

        if (gridCell != null) {
            gridCell.setColspan(colSpan);
        }

        if (getParent() != null) {
            if (getParent().getParent() instanceof DashboardGridLayout) {
                DashboardGridLayout parent = (DashboardGridLayout) getParent().getParent();
                parent.removeComponent(this);

                dashboardTools.removeEmptyLabelsForSpan(parent, gridCell);
                parent.addComponent(this, gridCell.getColumn(), gridCell.getRow(),
                        gridCell.getColumn() + gridCell.getColspan() - 1,
                        gridCell.getRow() + gridCell.getRowspan() - 1);
                dashboardTools.addEmptyLabels(parent, tree);
                dashboardTools.lockGridCells(parent, tree);
            }
        }
    }

    @Override
    public int getColSpan() {
        return colSpan;
    }

    @Override
    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
        GridCell gridCell = dashboardTools.getWidgetCell(tree, this);

        if (gridCell != null) {
            gridCell.setRowspan(rowSpan);
        }

        if (getParent() != null) {
            if (getParent().getParent() instanceof GridLayout) {
                DashboardGridLayout parent = (DashboardGridLayout) getParent().getParent();

                int availableColspan = parent.getColumns() - gridCell.getColumn();
                int availableRowspan = parent.getRows() - gridCell.getRowspan();

                parent.removeComponent(this);

                dashboardTools.removeEmptyLabelsForSpan(parent, gridCell);
                parent.addComponent(this, gridCell.getColumn(), gridCell.getRow(),
                        gridCell.getColumn() + gridCell.getColspan() - 1,
                        gridCell.getRow() + gridCell.getRowspan() - 1);
                dashboardTools.addEmptyLabels(parent, tree);
                dashboardTools.lockGridCells(parent, tree);
            }
        }
    }

    @Override
    public LayoutDragMode getDragMode() {
        return gridLayout.getDragMode();
    }

    @Override
    public void setDragMode(LayoutDragMode dragMode) {
        gridLayout.setDragMode(dragMode);
    }

    @Override
    public DragFilter getDragFilter() {
        return gridLayout.getDragFilter();
    }

    @Override
    public void setDragFilter(DragFilter dragFilter) {
        gridLayout.setDragFilter(dragFilter);
    }

    @Override
    public DragGrabFilter getDragGrabFilter() {
        return gridLayout.getDragGrabFilter();
    }

    @Override
    public void setDragGrabFilter(DragGrabFilter grabFilter) {
        gridLayout.setDragGrabFilter(grabFilter);
    }

    @Override
    public void setDragCaptionProvider(DragCaptionProvider provider) {
        gridLayout.setDragCaptionProvider(provider);
    }

    @Override
    public DragCaptionProvider getDragCaptionProvider() {
        return gridLayout.getDragCaptionProvider();
    }

    @Override
    public int getRowSpan() {
        return rowSpan;
    }

    @Override
    public Transferable getTransferable(Map<String, Object> rawVariables) {
        return gridLayout.getTransferable(rawVariables);
    }
}
