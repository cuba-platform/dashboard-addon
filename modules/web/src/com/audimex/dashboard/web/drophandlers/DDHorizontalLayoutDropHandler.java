/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.drophandlers;

import com.audimex.dashboard.entity.WidgetType;
import com.audimex.dashboard.web.dashboard.PaletteButton;
import com.audimex.dashboard.web.layouts.DashboardHorizontalLayout;
import com.audimex.dashboard.web.tools.DashboardTools;
import com.audimex.dashboard.web.tools.DashboardWidgetsFactory;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.components.Frame;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.ServerSideCriterion;
import com.vaadin.shared.ui.dd.HorizontalDropLocation;
import com.vaadin.ui.*;
import fi.jasoft.dragdroplayouts.DDHorizontalLayout;
import fi.jasoft.dragdroplayouts.drophandlers.DefaultHorizontalLayoutDropHandler;
import fi.jasoft.dragdroplayouts.events.LayoutBoundTransferable;

import java.util.function.Consumer;

public class DDHorizontalLayoutDropHandler extends DefaultHorizontalLayoutDropHandler {
    protected GridDropListener gridDropListener;
    protected Tree tree;
    protected Frame frame;
    protected Consumer<Tree> treeHandler;

    protected DashboardTools dashboardTools = AppBeans.get(DashboardTools.NAME);
    protected DashboardWidgetsFactory dashboardWidgetsFactory = AppBeans.get(DashboardWidgetsFactory.NAME);

    @Override
    protected void handleComponentReordering(DragAndDropEvent event) {
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event
                .getTransferable();
        DDHorizontalLayout.HorizontalLayoutTargetDetails details =
                (DDHorizontalLayout.HorizontalLayoutTargetDetails) event.getTargetDetails();
        DashboardHorizontalLayout layout = (DashboardHorizontalLayout) details.getTarget();

        if (!layout.isDropAllowed()) {
            return;
        }

        Component comp = transferable.getComponent();
        int idx = details.getOverIndex();

        dashboardTools.reorder(tree, layout.getParent(), comp, idx);
        treeHandler.accept(tree);
    }

    @Override
    protected void handleDropFromLayout(DragAndDropEvent event) {
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event.getTransferable();
        DDHorizontalLayout.HorizontalLayoutTargetDetails details =
                (DDHorizontalLayout.HorizontalLayoutTargetDetails) event.getTargetDetails();
        AbstractLayout targetLayout = (AbstractLayout) details.getTarget();

        if (targetLayout.getParent() instanceof DashboardHorizontalLayout) {
            targetLayout = (DashboardHorizontalLayout) targetLayout.getParent();
        }

        if (!((DashboardHorizontalLayout) targetLayout).isDropAllowed()) {
            return;
        }

        int idx = details.getOverIndex();
        Component component = transferable.getComponent();

        Component parent = targetLayout.getParent();
        while (parent != null) {
            if (parent == component) {
                return;
            }
            parent = parent.getParent();
        }

        HorizontalDropLocation loc = (details).getDropLocation();
        if (loc == HorizontalDropLocation.CENTER
                || loc == HorizontalDropLocation.RIGHT) {
            idx++;
        }

        if (transferable.getComponent() instanceof Button) {
            PaletteButton dragComponent = (PaletteButton) transferable.getComponent();

            component = dashboardWidgetsFactory.createWidgetOnDrop(dragComponent, tree, gridDropListener,
                    frame, treeHandler, targetLayout);

            if (dragComponent.getWidgetType() != WidgetType.GRID_LAYOUT) {
                if (idx >= 0) {
                    dashboardTools.addComponent(tree, targetLayout, component, idx);
                } else {
                    dashboardTools.addComponent(tree, targetLayout, component, 0);
                }
            }
        } else {
            if (component == targetLayout) {
                return;
            }

            if (idx >= 0) {
                if (targetLayout instanceof DashboardHorizontalLayout) {
                    ((DashboardHorizontalLayout) targetLayout).addComponent(component, idx);
                } else if (targetLayout instanceof AbstractOrderedLayout) {
                    ((AbstractOrderedLayout) targetLayout).addComponent(component, idx);
                }
            } else {
                targetLayout.addComponent(component);
                idx = 0;
            }

            dashboardTools.reorder(tree, details.getTarget().getParent(), component, idx);

            if (targetLayout instanceof DashboardHorizontalLayout) {
                ((DashboardHorizontalLayout) targetLayout).getMainLayout().setExpandRatio(component, 1);
            }
        }

        treeHandler.accept(tree);
    }

    @Override
    public AcceptCriterion getAcceptCriterion() {
        ServerSideCriterion serverSideCriterion = new ServerSideCriterion() {
            @Override
            public boolean accept(DragAndDropEvent dragEvent) {
                if (dragEvent.getTransferable().getSourceComponent() instanceof Tree) {
                    return false;
                }
                return true;
            }
        };
        return serverSideCriterion;
    }

    public void setTreeHandler(Consumer<Tree> treeHandler) {
        this.treeHandler = treeHandler;
    }

    public Frame getFrame() {
        return frame;
    }

    public void setFrame(Frame frame) {
        this.frame = frame;
    }

    public void setTree(Tree tree) {
        this.tree = tree;
    }

    public void setGridDropListener(GridDropListener gridDropListener) {
        this.gridDropListener = gridDropListener;
    }
}
