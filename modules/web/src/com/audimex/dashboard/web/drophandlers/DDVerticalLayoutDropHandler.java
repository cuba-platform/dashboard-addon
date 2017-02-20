/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.drophandlers;

import com.audimex.dashboard.web.utils.LayoutUtils;
import com.audimex.dashboard.web.utils.TreeUtils;
import com.audimex.dashboard.web.widgets.WidgetPanel;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.ui.*;
import fi.jasoft.dragdroplayouts.DDVerticalLayout;
import fi.jasoft.dragdroplayouts.drophandlers.DefaultVerticalLayoutDropHandler;
import fi.jasoft.dragdroplayouts.events.LayoutBoundTransferable;

import java.util.Map;

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
            Button dragComponent = (Button) transferable.getComponent();
            // Add component
            if (dragComponent.getId().equals("verticalLayout")) {
                comp = LayoutUtils.createVerticalDropLayout(componentDescriptorTree, gridDropListener);
            } else if (dragComponent.getId().equals("horizontalLayout")) {
                comp = LayoutUtils.createHorizontalDropLayout(componentDescriptorTree, gridDropListener);
            } else if (dragComponent.getId().equals("gridLayout")) {
                comp = LayoutUtils.createGridDropLayout(componentDescriptorTree, gridDropListener);
                gridDropListener.gridDropped((GridLayout) comp);
            } else if (dragComponent.getId().equals("widgetPanel")) {
                comp = new WidgetPanel();
                ((WidgetPanel) comp).setContent((Map<String, Object>) dragComponent.getData());
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
