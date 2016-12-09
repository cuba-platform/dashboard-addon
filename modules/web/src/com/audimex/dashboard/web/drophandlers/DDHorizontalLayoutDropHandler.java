/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.drophandlers;

import com.audimex.dashboard.web.ComponentStructure;
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

/**
 * Created by petunin on 07.12.2016.
 */
public class DDHorizontalLayoutDropHandler extends DefaultHorizontalLayoutDropHandler {
    protected ComponentStructure componentStructure;
    protected StructureChangeListener structureChangeListener;

    @Override
    protected void handleDropFromLayout(DragAndDropEvent event) {
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event
                .getTransferable();
        DDHorizontalLayout.HorizontalLayoutTargetDetails details = (DDHorizontalLayout.HorizontalLayoutTargetDetails) event
                .getTargetDetails();
        AbstractOrderedLayout layout = (AbstractOrderedLayout) details
                .getTarget();
        Component source = event.getTransferable().getSourceComponent();
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
                ((DDVerticalLayout) comp).setDragMode(LayoutDragMode.CLONE);
                ((DDVerticalLayout) comp).setSpacing(true);
                ((DDVerticalLayout) comp).setDropHandler(new DDVerticalLayoutDropHandler());
                ((DDVerticalLayoutDropHandler) ((DDVerticalLayout) comp).getDropHandler())
                        .setStructure(new ComponentStructure(comp, details.getTarget()));
                ((DDVerticalLayoutDropHandler) ((DDVerticalLayout) comp).getDropHandler())
                        .addStructureChangeListener(structureChangeListener);
                ((DDVerticalLayout) comp).setMargin(true);
            } else if (dragComponent.getId().equals("horizontalLayout")) {
                comp = new DDHorizontalLayout();
                comp.setSizeFull();
                comp.setStyleName("dd-bordering");
                ((DDHorizontalLayout) comp).setDragMode(LayoutDragMode.CLONE);
                ((DDHorizontalLayout) comp).setSpacing(true);
                ((DDHorizontalLayout) comp).setDropHandler(DDHorizontalLayoutDropHandler.this);
                ((DDHorizontalLayoutDropHandler) ((DDHorizontalLayout) comp).getDropHandler())
                        .setStructure(new ComponentStructure(comp, details.getTarget()));
                ((DDHorizontalLayoutDropHandler) ((DDHorizontalLayout) comp).getDropHandler())
                        .addStructureChangeListener(structureChangeListener);
                ((DDHorizontalLayout) comp).setMargin(true);
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
            handleReordering(comp);
        } else {
            ((AbstractOrderedLayout) details.getTarget()).addComponent(transferable.getComponent());
        }
    }

    public void setStructure(ComponentStructure componentStructure) {
        this.componentStructure = componentStructure;
    }

    protected void handleReordering(Component newComponent) {
        componentStructure.addChild(newComponent);
        structureChangeListener.structureChanged();
    }

    protected void addStructureChangeListener(StructureChangeListener structureChangeListener) {
        this.structureChangeListener = structureChangeListener;
    }
}
