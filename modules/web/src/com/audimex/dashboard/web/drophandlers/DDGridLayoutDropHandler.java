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
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.ServerSideCriterion;
import com.vaadin.ui.*;
import fi.jasoft.dragdroplayouts.DDGridLayout;
import fi.jasoft.dragdroplayouts.drophandlers.DefaultGridLayoutDropHandler;
import fi.jasoft.dragdroplayouts.events.LayoutBoundTransferable;

public class DDGridLayoutDropHandler extends DefaultGridLayoutDropHandler {
    private GridDropListener gridDropListener;
    private Tree componentDescriptorTree;

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
            // Add component
            if (dragComponent.getWidgetType() == WidgetType.VERTICAL_LAYOUT) {
                component = LayoutUtils.createVerticalDropLayout(componentDescriptorTree, gridDropListener);
                ((DashboardVerticalLayout) component).setParentFrame(dragComponent.getDropFrame());
            } else if (dragComponent.getWidgetType() == WidgetType.HORIZONTAL_LAYOUT) {
                component = LayoutUtils.createHorizontalDropLayout(componentDescriptorTree, gridDropListener);
                ((DashboardHorizontalLayout) component).setParentFrame(dragComponent.getDropFrame());
            } else if (dragComponent.getWidgetType() == WidgetType.GRID_LAYOUT) {
                component = LayoutUtils.createGridDropLayout(componentDescriptorTree, gridDropListener);
                gridDropListener.gridDropped((GridLayout) component);
            } else if (dragComponent.getWidgetType() == WidgetType.FRAME_PANEL) {
                component = new WidgetPanel(componentDescriptorTree);
                WidgetPanel widgetPanel = (WidgetPanel) component;
                widgetPanel.setParentFrame(dragComponent.getDropFrame());
                widgetPanel.setContent(dragComponent.getWidget());
            }

            if (component instanceof LayoutEvents.LayoutClickNotifier) {
                ((LayoutEvents.LayoutClickNotifier) component).addLayoutClickListener(
                        (LayoutEvents.LayoutClickListener) event1 ->
                                componentDescriptorTree.setValue(event1.getChildComponent())
                );
            }

            TreeUtils.addComponent(componentDescriptorTree, details.getTarget(), component, 0);
            if (layout.getComponent(column, row) instanceof Label) {
                layout.removeComponent(column, row);
            }
            addComponent(event, component, column, row);
        } else {
            TreeUtils.addComponent(componentDescriptorTree, details.getTarget(), component, 0);
            addComponent(event, component, column, row);
        }
    }

    public void setComponentDescriptorTree(Tree componentDescriptorTree) {
        this.componentDescriptorTree = componentDescriptorTree;
    }

    public void setGridDropListener(GridDropListener gridDropListener) {
        this.gridDropListener = gridDropListener;
    }

    public GridDropListener getGridDropListener() {
        return gridDropListener;
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
                if (insideComponent != null && !(insideComponent instanceof Label)) {
                    return false;
                }
                return true;
            }
        };
        return serverSideCriterion;
    }
}
