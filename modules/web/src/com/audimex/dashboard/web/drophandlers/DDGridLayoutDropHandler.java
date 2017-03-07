/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.drophandlers;

import com.audimex.dashboard.entity.WidgetType;
import com.audimex.dashboard.web.dashboard.PaletteButton;
import com.audimex.dashboard.web.layouts.DashboardGridLayout;
import com.audimex.dashboard.web.tools.DashboardTools;
import com.audimex.dashboard.web.tools.DashboardWidgetsFactory;
import com.audimex.dashboard.web.widgets.GridCell;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.components.Frame;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.ServerSideCriterion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Tree;
import fi.jasoft.dragdroplayouts.DDGridLayout;
import fi.jasoft.dragdroplayouts.drophandlers.DefaultGridLayoutDropHandler;
import fi.jasoft.dragdroplayouts.events.LayoutBoundTransferable;

import java.util.function.Consumer;

public class DDGridLayoutDropHandler extends DefaultGridLayoutDropHandler {
    protected GridDropListener gridDropListener;
    protected Tree tree;
    protected Frame frame;
    protected Consumer<Tree> treeHandler;

    protected DashboardTools dashboardTools = AppBeans.get(DashboardTools.NAME);
    protected DashboardWidgetsFactory dashboardWidgetsFactory = AppBeans.get(DashboardWidgetsFactory.NAME);

    @Override
    protected void handleComponentReordering(DragAndDropEvent event) {
        DDGridLayout.GridLayoutTargetDetails details = (DDGridLayout.GridLayoutTargetDetails) event
                .getTargetDetails();
        DashboardGridLayout layout = (DashboardGridLayout) details.getTarget();
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event
                .getTransferable();
        Component comp = transferable.getComponent();

        int row = details.getOverRow();
        int column = details.getOverColumn();

        super.handleComponentReordering(event);

        GridCell gridCell = dashboardTools.getGridCell(tree, tree.getChildren(layout), column, row);
        if (gridCell != null) {
            dashboardTools.moveComponent(tree, gridCell, comp, 0);
        }

        treeHandler.accept(tree);
    }

    @Override
    protected void handleDropFromLayout(DragAndDropEvent event) {
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event.getTransferable();
        DDGridLayout.GridLayoutTargetDetails details = (DDGridLayout.GridLayoutTargetDetails) event.getTargetDetails();
        DDGridLayout layout = (DDGridLayout) details.getTarget();

        int row = details.getOverRow();
        int column = details.getOverColumn();
        Component component = transferable.getComponent();

        Component parent = layout.getParent();
        while (parent != null) {
            if (parent == component) {
                return;
            }
            parent = parent.getParent();
        }

        if (transferable.getComponent() instanceof Button) {
            PaletteButton dragComponent = (PaletteButton) transferable.getComponent();

            component = dashboardWidgetsFactory.createWidgetOnDrop(dragComponent, tree, gridDropListener,
                    frame, treeHandler, layout);

            if (dragComponent.getWidgetType() != WidgetType.GRID_LAYOUT) {
                Component target = null;
                if (details.getTarget() instanceof GridLayout) {
                    GridLayout gridLayout = (GridLayout) details.getTarget();
                    target = dashboardTools.getGridCell(
                            tree,
                            tree.getChildren(gridLayout.getParent()),
                            column,
                            row);
                } else {
                    target = details.getTarget();
                }
                dashboardTools.addComponent(tree, target, component, 0);
            }
        } else {
            GridCell gridCell = dashboardTools.getGridCell(tree, tree.getChildren(layout.getParent()), column, row);
            if (gridCell != null) {
                dashboardTools.moveComponent(tree, gridCell, component, 0);
            }
        }


        treeHandler.accept(tree);
    }

    public void setTreeHandler(Consumer<Tree> treeHandler) {
        this.treeHandler = treeHandler;
    }

    public void setTree(Tree tree) {
        this.tree = tree;
    }

    public void setGridDropListener(GridDropListener gridDropListener) {
        this.gridDropListener = gridDropListener;
    }

    public GridDropListener getGridDropListener() {
        return gridDropListener;
    }

    public Frame getFrame() {
        return frame;
    }

    public void setFrame(Frame frame) {
        this.frame = frame;
    }

    @Override
    public AcceptCriterion getAcceptCriterion() {
        ServerSideCriterion serverSideCriterion = new ServerSideCriterion() {
            @Override
            public boolean accept(DragAndDropEvent dragEvent) {
                DDGridLayout.GridLayoutTargetDetails targetDetails =
                        (DDGridLayout.GridLayoutTargetDetails) dragEvent.getTargetDetails();
                Component insideComponent = ((GridLayout) targetDetails.getTarget())
                                                .getComponent(targetDetails.getOverColumn(), targetDetails.getOverRow());
                if (insideComponent != null && !(insideComponent instanceof GridCell)) {
                    return false;
                }
                return true;
            }
        };
        return serverSideCriterion;
    }
}
