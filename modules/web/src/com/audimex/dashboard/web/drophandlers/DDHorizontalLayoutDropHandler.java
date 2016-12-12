/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.drophandlers;

import com.audimex.dashboard.web.WidgetPanel;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.server.ClientConnector;
import com.vaadin.shared.ui.dd.HorizontalDropLocation;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import fi.jasoft.dragdroplayouts.DDHorizontalLayout;
import fi.jasoft.dragdroplayouts.DDVerticalLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;
import fi.jasoft.dragdroplayouts.drophandlers.DefaultHorizontalLayoutDropHandler;
import fi.jasoft.dragdroplayouts.events.LayoutBoundTransferable;

public class DDHorizontalLayoutDropHandler extends DefaultHorizontalLayoutDropHandler {
    protected StructureChangeListener structureChangeListener;

    @Override
    protected void handleDropFromLayout(DragAndDropEvent event) {
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event.getTransferable();
        DDHorizontalLayout.HorizontalLayoutTargetDetails details =
                (DDHorizontalLayout.HorizontalLayoutTargetDetails) event.getTargetDetails();
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

        HorizontalDropLocation loc = (details).getDropLocation();
        if (loc == HorizontalDropLocation.CENTER
                || loc == HorizontalDropLocation.RIGHT) {
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
                ddVerticalLayout.setDropHandler(new DDVerticalLayoutDropHandler());

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
                ddHorizontalLayout.setDropHandler(DDHorizontalLayoutDropHandler.this);

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

    protected void addStructureChangeListener(StructureChangeListener structureChangeListener) {
        this.structureChangeListener = structureChangeListener;
    }
}
