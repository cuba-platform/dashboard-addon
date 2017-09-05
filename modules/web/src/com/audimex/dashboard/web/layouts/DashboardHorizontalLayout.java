package com.audimex.dashboard.web.layouts;

import com.audimex.dashboard.web.drophandlers.DDHorizontalLayoutDropHandler;
import com.audimex.dashboard.web.drophandlers.GridDropListener;
import com.audimex.dashboard.web.tools.DashboardTools;
import com.audimex.dashboard.web.widgets.GridCell;
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
import fi.jasoft.dragdroplayouts.DDHorizontalLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;
import fi.jasoft.dragdroplayouts.interfaces.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class DashboardHorizontalLayout extends CssLayout implements HasMainLayout, HasWeight,
        HasGridSpan, DragGrabFilterSupport, HasDragCaptionProvider, LayoutDragSource, HasDragCaption {
    protected DDHorizontalLayout horizontalLayout = null;
    protected int weight = 1;
    protected int colSpan = 1;
    protected int rowSpan = 1;
    protected Tree tree = null;
    protected Frame parentFrame = null;

    protected Messages messages = AppBeans.get(Messages.NAME);

    protected String icon;
    protected String caption;

    protected DashboardTools dashboardTools;

    public DashboardHorizontalLayout(Tree tree, GridDropListener gridDropListener,
                                     Frame frame, Consumer<Tree> treeHandler) {
        this.tree = tree;
        dashboardTools = AppBeans.get(DashboardTools.NAME);

        HorizontalLayout buttonsPanel = new HorizontalLayout();
        Button configButton = new Button(WebComponentsHelper.getIcon("icons/gear.png"));
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
        removeButton.addClickListener(event -> {
            dashboardTools.removeComponent(tree, tree.getValue());
            treeHandler.accept(tree);
        });
        configButton.setDescription(messages.getMainMessage("dashboard.configButton"));
        removeButton.setDescription(messages.getMainMessage("dashboard.removeButton"));
        buttonsPanel.addComponent(configButton);
        buttonsPanel.addComponent(removeButton);
        buttonsPanel.addStyleName(DashboardTools.AMXD_LAYOUT_CONTROLS);

        horizontalLayout = new DDHorizontalLayout();
        DDHorizontalLayoutDropHandler ddVerticalLayoutDropHandler = new DDHorizontalLayoutDropHandler();
        ddVerticalLayoutDropHandler.setGridDropListener(gridDropListener);
        ddVerticalLayoutDropHandler.setFrame(frame);
        ddVerticalLayoutDropHandler.setTreeHandler(treeHandler);
        ddVerticalLayoutDropHandler.setTree(tree);

        horizontalLayout.setDragMode(LayoutDragMode.CLONE);
        horizontalLayout.setSizeFull();
        horizontalLayout.setSpacing(true);
        horizontalLayout.setMargin(true);
        horizontalLayout.setDropHandler(ddVerticalLayoutDropHandler);
        horizontalLayout.addStyleName("amxd-layout-content");

        super.addComponent(buttonsPanel);
        super.addComponent(horizontalLayout);
    }

    public Frame getParentFrame() {
        return parentFrame;
    }

    public void setParentFrame(Frame parentFrame) {
        this.parentFrame = parentFrame;
    }

    @Override
    public void addComponent(Component c) {
        horizontalLayout.addComponent(c);
    }

    @Override
    public void addComponent(Component c, int index) {
        horizontalLayout.addComponent(c, index);
    }

    @Override
    public AbstractOrderedLayout getMainLayout() {
        return horizontalLayout;
    }

    @Override
    public void setMargin(boolean margin) {
        horizontalLayout.setMargin(margin);
    }

    @Override
    public void setWeight(int weight) {
        this.weight = weight;

        if (getParent() instanceof AbstractOrderedLayout) {
            ((AbstractOrderedLayout) getParent()).setExpandRatio(this, weight);
        }
    }

    @Override
    public void addLayoutClickListener(LayoutEvents.LayoutClickListener listener) {
        horizontalLayout.addLayoutClickListener(listener);
    }

    @Override
    public void removeLayoutClickListener(LayoutEvents.LayoutClickListener listener) {
        horizontalLayout.removeLayoutClickListener(listener);
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
            if (getParent().getParent() instanceof DashboardGridLayout) {
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
        return horizontalLayout.getDragMode();
    }

    @Override
    public void setDragMode(LayoutDragMode dragMode) {
        horizontalLayout.setDragMode(dragMode);
    }

    @Override
    public DragFilter getDragFilter() {
        return horizontalLayout.getDragFilter();
    }

    @Override
    public void setDragFilter(DragFilter dragFilter) {
        horizontalLayout.setDragFilter(dragFilter);
    }

    @Override
    public DragGrabFilter getDragGrabFilter() {
        return horizontalLayout.getDragGrabFilter();
    }

    @Override
    public void setDragGrabFilter(DragGrabFilter grabFilter) {
        horizontalLayout.setDragGrabFilter(grabFilter);
    }

    @Override
    public void setDragCaptionProvider(DragCaptionProvider provider) {
        horizontalLayout.setDragCaptionProvider(provider);
    }

    @Override
    public DragCaptionProvider getDragCaptionProvider() {
        return horizontalLayout.getDragCaptionProvider();
    }

    @Override
    public int getRowSpan() {
        return rowSpan;
    }

    @Override
    public Transferable getTransferable(Map<String, Object> rawVariables) {
        return horizontalLayout.getTransferable(rawVariables);
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
}
