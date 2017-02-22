/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.drophandlers;

import com.audimex.dashboard.entity.WidgetType;
import com.audimex.dashboard.web.palette.PaletteButton;
import com.audimex.dashboard.web.utils.LayoutUtils;
import com.audimex.dashboard.web.utils.TreeUtils;
import com.audimex.dashboard.web.widgets.GridCell;
import com.audimex.dashboard.web.widgets.WidgetPanel;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.ui.*;
import fi.jasoft.dragdroplayouts.DDVerticalLayout;
import fi.jasoft.dragdroplayouts.drophandlers.DefaultVerticalLayoutDropHandler;
import fi.jasoft.dragdroplayouts.events.LayoutBoundTransferable;

public class DDVerticalLayoutDropHandler extends DefaultVerticalLayoutDropHandler {
    private GridDropListener gridDropListener;
    private Tree componentDescriptorTree;

    @Override
    protected void handleDropFromLayout(DragAndDropEvent event) {
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event.getTransferable();
        DDVerticalLayout.VerticalLayoutTargetDetails details =
                (DDVerticalLayout.VerticalLayoutTargetDetails) event.getTargetDetails();
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

        VerticalDropLocation loc = (details).getDropLocation();
        if (loc == VerticalDropLocation.MIDDLE
                || loc == VerticalDropLocation.BOTTOM) {
            idx++;
        }

        if (transferable.getComponent() instanceof Button) {
            PaletteButton dragComponent = (PaletteButton) transferable.getComponent();
            // Add component
            if (dragComponent.getWidgetType() == WidgetType.VERTICAL_LAYOUT) {
                comp = LayoutUtils.createVerticalDropLayout(componentDescriptorTree, gridDropListener);
            } else if (dragComponent.getWidgetType() == WidgetType.HORIZONTAL_LAYOUT) {
                comp = LayoutUtils.createHorizontalDropLayout(componentDescriptorTree, gridDropListener);
            } else if (dragComponent.getWidgetType() == WidgetType.GRID_LAYOUT) {
                comp = LayoutUtils.createGridDropLayout(componentDescriptorTree, gridDropListener);
                gridDropListener.gridDropped((GridLayout) comp);
            } else if (dragComponent.getWidgetType() == WidgetType.FRAME_PANEL) {
                comp = new WidgetPanel(componentDescriptorTree);
                ((WidgetPanel) comp).setContent(dragComponent.getWidget(), dragComponent.getDropFrame());
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
            if (idx >= 0) {
                targetLayout.addComponent(comp, idx);
            } else {
                targetLayout.addComponent(comp);
            }
            TreeUtils.reorder(componentDescriptorTree, details.getTarget(), comp, 0);
            targetLayout.setExpandRatio(comp, 1);
            GridCell gridCell = (GridCell) targetLayout;
            TreeUtils.markGridCells(componentDescriptorTree, (GridLayout) gridCell.getParent(), gridCell.getRow(), gridCell.getColumn(), 1, 1);
        }

        ((TreeDropHandler) componentDescriptorTree.getDropHandler()).getTreeChangeListener().treeChanged();
    }

    public void setComponentDescriptorTree(Tree componentDescriptorTree) {
        this.componentDescriptorTree = componentDescriptorTree;
    }

    public void setGridDropListener(GridDropListener gridDropListener) {
        this.gridDropListener = gridDropListener;
    }
}
