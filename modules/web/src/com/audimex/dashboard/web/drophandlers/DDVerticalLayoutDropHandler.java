/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.drophandlers;

import com.audimex.dashboard.web.WidgetPanel;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.server.ClientConnector;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import fi.jasoft.dragdroplayouts.DDHorizontalLayout;
import fi.jasoft.dragdroplayouts.DDVerticalLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;
import fi.jasoft.dragdroplayouts.drophandlers.DefaultVerticalLayoutDropHandler;
import fi.jasoft.dragdroplayouts.events.LayoutBoundTransferable;

public class DDVerticalLayoutDropHandler extends DefaultVerticalLayoutDropHandler {
    protected StructureChangeListener structureChangeListener;

    @Override
    protected void handleDropFromLayout(DragAndDropEvent event) {
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event.getTransferable();
        DDVerticalLayout.VerticalLayoutTargetDetails details =
                (DDVerticalLayout.VerticalLayoutTargetDetails) event.getTargetDetails();
        AbstractOrderedLayout layout =
                (AbstractOrderedLayout) details.getTarget();

        int idx = (details).getOverIndex();
        Component comp = transferable.getComponent();

        Component parent = layout.getParent();
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
                comp = new DDVerticalLayout();
                comp.setSizeFull();
                comp.setStyleName("dd-bordering");

                DDVerticalLayout ddVerticalLayout = (DDVerticalLayout) comp;
                ddVerticalLayout.setDragMode(LayoutDragMode.CLONE);
                ddVerticalLayout.setSpacing(true);
                ddVerticalLayout.setMargin(true);
                ddVerticalLayout.setDropHandler(DDVerticalLayoutDropHandler.this);

                ((DDVerticalLayoutDropHandler) ddVerticalLayout.getDropHandler())
                        .addStructureChangeListener(structureChangeListener);
            } else if (dragComponent.getId().equals("horizontalLayout")) {
                comp = new DDHorizontalLayout();
                comp.setSizeFull();
                comp.setStyleName("dd-bordering");

                DDHorizontalLayout ddHorizontalLayout = (DDHorizontalLayout) comp;
                ddHorizontalLayout.setDragMode(LayoutDragMode.CLONE);
                ddHorizontalLayout.setSpacing(true);
                ddHorizontalLayout.setMargin(true);
                ddHorizontalLayout.setDropHandler(new DDHorizontalLayoutDropHandler());

                ((DDHorizontalLayoutDropHandler) ddHorizontalLayout.getDropHandler())
                        .addStructureChangeListener(structureChangeListener);
            } else if (dragComponent.getId().equals("widgetPanel")) {
                comp = new WidgetPanel("Panel");
                comp.setSizeFull();
            }

            if (idx >= 0) {
                ((AbstractOrderedLayout) details.getTarget()).addComponent(comp, idx);
            } else {
                ((AbstractOrderedLayout) details.getTarget()).addComponent(comp);
            }
            comp.addDetachListener((ClientConnector.DetachListener) event1 -> structureChangeListener.structureChanged());
            structureChangeListener.structureChanged();
        } else {
            ((AbstractOrderedLayout) details.getTarget()).addComponent(transferable.getComponent());
        }
    }

    public void addStructureChangeListener(StructureChangeListener structureChangeListener) {
        this.structureChangeListener = structureChangeListener;
    }
}
