/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.drophandlers;

import com.audimex.dashboard.entity.WidgetType;
import com.audimex.dashboard.web.palette.PaletteButton;
import com.audimex.dashboard.web.utils.LayoutUtils;
import com.audimex.dashboard.web.utils.TreeUtils;
import com.audimex.dashboard.web.widgets.WidgetPanel;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.dd.DragAndDropEvent;
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
        AbstractOrderedLayout targetLayout = (AbstractOrderedLayout) details.getTarget();

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
            } else if (dragComponent.getWidgetType() == WidgetType.HORIZONTAL_LAYOUT) {
                comp = LayoutUtils.createVerticalDropLayout(componentDescriptorTree, gridDropListener);
            } else if (dragComponent.getWidgetType() == WidgetType.GRID_LAYOUT) {
                comp = LayoutUtils.createGridDropLayout(componentDescriptorTree, gridDropListener);
                gridDropListener.gridDropped((GridLayout) comp);
            } else if (dragComponent.getWidgetType() == WidgetType.FRAME_PANEL) {
                comp = new WidgetPanel(componentDescriptorTree);
                ((WidgetPanel) comp).setContent(dragComponent.getWidget(), dragComponent.getDropFrame());
                comp.setSizeFull();
            }

            if (comp instanceof LayoutEvents.LayoutClickNotifier) {
                ((LayoutEvents.LayoutClickNotifier) comp).addLayoutClickListener(
                        (LayoutEvents.LayoutClickListener) e ->
                                componentDescriptorTree.setValue(e.getClickedComponent())
                );
            }

            if (idx >= 0) {
                TreeUtils.addComponent(componentDescriptorTree, details.getTarget(), comp, idx);
                targetLayout.addComponent(comp, idx);
            } else {
                TreeUtils.addComponent(componentDescriptorTree, details.getTarget(), comp, 0);
                targetLayout.addComponent(comp);
            }

            targetLayout.setExpandRatio(comp, 1);
        } else {
            targetLayout.addComponent(transferable.getComponent());
            TreeUtils.reorder(componentDescriptorTree, details.getTarget(), comp, 0);
            targetLayout.setExpandRatio(transferable.getComponent(), 1);
        }
    }

    public void setComponentDescriptorTree(Tree componentDescriptorTree) {
        this.componentDescriptorTree = componentDescriptorTree;
    }

    public void setGridDropListener(GridDropListener gridDropListener) {
        this.gridDropListener = gridDropListener;
    }
}
