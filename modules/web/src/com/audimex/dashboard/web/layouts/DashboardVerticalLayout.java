package com.audimex.dashboard.web.layouts;

import com.audimex.dashboard.web.drophandlers.DDVerticalLayoutDropHandler;
import com.audimex.dashboard.web.drophandlers.GridDropListener;
import com.audimex.dashboard.web.utils.DashboardUtils;
import com.audimex.dashboard.web.utils.LayoutUtils;
import com.audimex.dashboard.web.widgets.GridCell;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Frame;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.Transferable;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import fi.jasoft.dragdroplayouts.DDVerticalLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;
import fi.jasoft.dragdroplayouts.interfaces.*;

import java.util.HashMap;
import java.util.Map;

public class DashboardVerticalLayout extends CssLayout implements HasMainLayout, HasWeight, HasGridSpan,
        DragGrabFilterSupport, HasDragCaptionProvider, LayoutDragSource {
    private int weight = 1;
    private int colSpan = 1;
    private int rowSpan = 1;
    private Tree tree = null;
    private DDVerticalLayout verticalLayout = null;
    private Frame parentFrame = null;

    public DashboardVerticalLayout(Tree tree, GridDropListener gridDropListener) {
        this.tree = tree;
        HorizontalLayout buttonsPanel = new HorizontalLayout();
        Button button = new Button(FontAwesome.GEARS);
        button.addClickListener((Button.ClickListener) (event) -> {
            Map<String, Object> params = new HashMap<>();
            params.put("widget", this);
            params.put("tree", tree);
            parentFrame.openWindow("dashboardElementConfig", WindowManager.OpenType.DIALOG, params);
        });
        buttonsPanel.addComponent(button);
        buttonsPanel.addStyleName("dd-layout-controls");

        verticalLayout = new DDVerticalLayout();
        DDVerticalLayoutDropHandler ddVerticalLayoutDropHandler = new DDVerticalLayoutDropHandler();
        ddVerticalLayoutDropHandler.setGridDropListener(gridDropListener);
        ddVerticalLayoutDropHandler.setComponentDescriptorTree(tree);

        verticalLayout.setDragMode(DashboardUtils.getDefaultDragMode());
        verticalLayout.setSizeFull();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(true);
        verticalLayout.setDropHandler(ddVerticalLayoutDropHandler);
        verticalLayout.addStyleName("dd-layout-content");

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

    public Frame getParentFrame() {
        return parentFrame;
    }

    public void setParentFrame(Frame parentFrame) {
        this.parentFrame = parentFrame;
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
        GridCell gridCell = LayoutUtils.getWidgetCell(tree, this);

        if (gridCell != null) {
            gridCell.setColspan(colSpan);
        }

        if (getParent() instanceof GridLayout) {
            GridLayout parent = (GridLayout) getParent();
            parent.removeComponent(this);

            DashboardUtils.removeEmptyLabelsForSpan(parent, gridCell);
            parent.addComponent(this, gridCell.getColumn(), gridCell.getRow(),
                    gridCell.getColumn() + gridCell.getColspan() - 1,
                    gridCell.getRow() + gridCell.getRowspan() - 1);
            DashboardUtils.addEmptyLabelsToLayout(parent, tree);
            DashboardUtils.lockGridCells(parent, tree);
        }
    }

    @Override
    public int getColSpan() {
        return colSpan;
    }

    @Override
    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
        GridCell gridCell = LayoutUtils.getWidgetCell(tree, this);

        if (gridCell != null) {
            gridCell.setRowspan(rowSpan);
        }

        if (getParent() instanceof GridLayout) {
            GridLayout parent = (GridLayout) getParent();

            int availableColspan = parent.getColumns() - gridCell.getColumn();
            int availableRowspan = parent.getRows() - gridCell.getRowspan();

            parent.removeComponent(this);

            DashboardUtils.removeEmptyLabelsForSpan(parent, gridCell);
            parent.addComponent(this, gridCell.getColumn(), gridCell.getRow(),
                    gridCell.getColumn() + gridCell.getColspan() - 1,
                    gridCell.getRow() + gridCell.getRowspan() - 1);
            DashboardUtils.addEmptyLabelsToLayout(parent, tree);
            DashboardUtils.lockGridCells(parent, tree);
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
    public Transferable getTransferable(Map<String, Object> rawVariables) {
        return verticalLayout.getTransferable(rawVariables);
    }
}
