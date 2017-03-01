/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.drophandlers;

import com.audimex.dashboard.entity.WidgetType;
import com.audimex.dashboard.web.layouts.DashboardHorizontalLayout;
import com.audimex.dashboard.web.layouts.DashboardVerticalLayout;
import com.audimex.dashboard.web.palette.PaletteButton;
import com.audimex.dashboard.web.utils.LayoutUtils;
import com.audimex.dashboard.web.utils.TreeUtils;
import com.audimex.dashboard.web.widgets.WidgetPanel;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.ServerSideCriterion;
import com.vaadin.shared.ui.dd.HorizontalDropLocation;
import com.vaadin.ui.*;
import fi.jasoft.dragdroplayouts.DDHorizontalLayout;
import fi.jasoft.dragdroplayouts.drophandlers.DefaultHorizontalLayoutDropHandler;
import fi.jasoft.dragdroplayouts.events.LayoutBoundTransferable;

public class DDHorizontalLayoutDropHandler extends DefaultHorizontalLayoutDropHandler {
    protected GridDropListener gridDropListener;
    protected Tree componentDescriptorTree;

    @Override
    protected void handleDropFromLayout(DragAndDropEvent event) {
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event.getTransferable();
        DDHorizontalLayout.HorizontalLayoutTargetDetails details =
                (DDHorizontalLayout.HorizontalLayoutTargetDetails) event.getTargetDetails();
        AbstractLayout targetLayout = (AbstractLayout) details.getTarget();

        if (targetLayout.getParent() instanceof DashboardHorizontalLayout) {
            targetLayout = (DashboardHorizontalLayout) targetLayout.getParent();
        }

        int idx = (details).getOverIndex();
        Component comp = transferable.getComponent();

        Component parent = targetLayout.getParent();
        while (parent != null) {
            if (parent == comp) {
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
            // Add component
            if (dragComponent.getWidgetType() == WidgetType.VERTICAL_LAYOUT) {
                comp = LayoutUtils.createVerticalDropLayout(componentDescriptorTree, gridDropListener);
                ((DashboardVerticalLayout) comp).setParentFrame(dragComponent.getDropFrame());
            } else if (dragComponent.getWidgetType() == WidgetType.HORIZONTAL_LAYOUT) {
                comp = LayoutUtils.createHorizontalDropLayout(componentDescriptorTree, gridDropListener);
                ((DashboardHorizontalLayout) comp).setParentFrame(dragComponent.getDropFrame());
            } else if (dragComponent.getWidgetType() == WidgetType.GRID_LAYOUT) {
                comp = LayoutUtils.createGridDropLayout(componentDescriptorTree, gridDropListener);
                gridDropListener.gridDropped((GridLayout) comp);
            } else if (dragComponent.getWidgetType() == WidgetType.FRAME_PANEL) {
                comp = new WidgetPanel(componentDescriptorTree);
                WidgetPanel widgetPanel = (WidgetPanel) comp;
                widgetPanel.setParentFrame(dragComponent.getDropFrame());
                widgetPanel.setContent(dragComponent.getWidget());
                comp.setSizeFull();
            }

            if (idx >= 0) {
                TreeUtils.addComponent(componentDescriptorTree, targetLayout, comp, idx);
            } else {
                TreeUtils.addComponent(componentDescriptorTree, targetLayout, comp, 0);
            }
        } else {
            TreeUtils.reorder(componentDescriptorTree, details.getTarget(), comp, 0);
        }

        ((TreeDropHandler) componentDescriptorTree.getDropHandler()).getTreeChangeListener().treeChanged();
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

    public void setComponentDescriptorTree(Tree componentDescriptorTree) {
        this.componentDescriptorTree = componentDescriptorTree;
    }

    public void setGridDropListener(GridDropListener gridDropListener) {
        this.gridDropListener = gridDropListener;
    }
}
