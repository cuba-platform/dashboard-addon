package com.audimex.dashboard.web.layouts;

import com.audimex.dashboard.web.drophandlers.DDVerticalLayoutDropHandler;
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
import fi.jasoft.dragdroplayouts.DDVerticalLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;
import fi.jasoft.dragdroplayouts.interfaces.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class DashboardVerticalLayout extends CssLayout implements HasMainLayout, HasWeight, HasGridSpan,
        DragGrabFilterSupport, HasDragCaptionProvider, LayoutDragSource, HasDragCaption {
    protected int weight = 1;
    protected int colSpan = 1;
    protected int rowSpan = 1;

    protected String caption;
    protected String icon;

    protected Messages messages = AppBeans.get(Messages.NAME);

    protected Tree tree = null;
    protected DDVerticalLayout verticalLayout = null;

    protected DashboardTools dashboardTools;

    public DashboardVerticalLayout(Tree tree, GridDropListener gridDropListener, Frame frame, Consumer<Tree> treeHandler) {
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
            WindowInfo windowInfo = windowConfig.getWindowInfo("widgetConfigWindow");
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

        verticalLayout = new DDVerticalLayout();
        DDVerticalLayoutDropHandler ddVerticalLayoutDropHandler = new DDVerticalLayoutDropHandler();
        ddVerticalLayoutDropHandler.setGridDropListener(gridDropListener);
        ddVerticalLayoutDropHandler.setTreeHandler(treeHandler);
        ddVerticalLayoutDropHandler.setFrame(frame);
        ddVerticalLayoutDropHandler.setTree(tree);

        verticalLayout.setDragMode(LayoutDragMode.CLONE);
        verticalLayout.setSizeFull();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(true);
        verticalLayout.setDropHandler(ddVerticalLayoutDropHandler);
        verticalLayout.addStyleName("amxd-layout-content");

        super.addComponent(buttonsPanel);
        super.addComponent(verticalLayout);
    }

    @Override
    public void addComponent(Component c) {
        verticalLayout.addComponent(c);
    }

    @Override
    public void addComponent(Component c, int index) {
        verticalLayout.addComponent(c, index);
    }

    @Override
    public AbstractOrderedLayout getMainLayout() {
        return verticalLayout;
    }

    @Override
    public void setMargin(boolean margin) {
        verticalLayout.setMargin(margin);
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
        verticalLayout.addLayoutClickListener(listener);
    }

    @Override
    public void removeLayoutClickListener(LayoutEvents.LayoutClickListener listener) {
        verticalLayout.removeLayoutClickListener(listener);
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
        return verticalLayout.getDragMode();
    }

    @Override
    public void setDragMode(LayoutDragMode dragMode) {
        verticalLayout.setDragMode(dragMode);
    }

    @Override
    public DragFilter getDragFilter() {
        return verticalLayout.getDragFilter();
    }

    @Override
    public void setDragFilter(DragFilter dragFilter) {
        verticalLayout.setDragFilter(dragFilter);
    }

    @Override
    public DragGrabFilter getDragGrabFilter() {
        return verticalLayout.getDragGrabFilter();
    }

    @Override
    public void setDragGrabFilter(DragGrabFilter grabFilter) {
        verticalLayout.setDragGrabFilter(grabFilter);
    }

    @Override
    public void setDragCaptionProvider(DragCaptionProvider provider) {
        verticalLayout.setDragCaptionProvider(provider);
    }

    @Override
    public DragCaptionProvider getDragCaptionProvider() {
        return verticalLayout.getDragCaptionProvider();
    }

    @Override
    public int getRowSpan() {
        return rowSpan;
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

    @Override
    public Transferable getTransferable(Map<String, Object> rawVariables) {
        return verticalLayout.getTransferable(rawVariables);
    }
}
