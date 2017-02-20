/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.drophandlers;

import com.audimex.dashboard.web.utils.LayoutUtils;
import com.audimex.dashboard.web.utils.TreeUtils;
import com.audimex.dashboard.web.widgets.WidgetPanel;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.shared.ui.dd.HorizontalDropLocation;
import com.vaadin.ui.*;
import fi.jasoft.dragdroplayouts.DDHorizontalLayout;
import fi.jasoft.dragdroplayouts.drophandlers.DefaultHorizontalLayoutDropHandler;
import fi.jasoft.dragdroplayouts.events.LayoutBoundTransferable;

import java.util.Map;

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
            Button dragComponent = (Button) transferable.getComponent();
            // Add component
            if (dragComponent.getId().equals("verticalLayout")) {
                comp = LayoutUtils.createVerticalDropLayout(componentDescriptorTree, gridDropListener);
            } else if (dragComponent.getId().equals("horizontalLayout")) {
                comp = LayoutUtils.createVerticalDropLayout(componentDescriptorTree, gridDropListener);
            } else if (dragComponent.getId().equals("gridLayout")) {
                comp = LayoutUtils.createGridDropLayout(componentDescriptorTree, gridDropListener);
                gridDropListener.gridDropped((GridLayout) comp);
            } else if (dragComponent.getId().equals("widgetPanel")) {
                comp = new WidgetPanel();
                ((WidgetPanel) comp).setContent((Map<String, Object>) dragComponent.getData());
                comp.setSizeFull();
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
