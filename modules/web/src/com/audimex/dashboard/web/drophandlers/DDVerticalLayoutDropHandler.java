/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.drophandlers;

import com.audimex.dashboard.web.ComponentStructure;
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

/**
 * Created by petunin on 06.12.2016.
 */
public class DDVerticalLayoutDropHandler extends DefaultVerticalLayoutDropHandler {
    protected ComponentStructure componentStructure;
    protected StructureChangeListener structureChangeListener;

    @Override
    protected void handleDropFromLayout(DragAndDropEvent event) {
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event
                .getTransferable();
        DDVerticalLayout.VerticalLayoutTargetDetails details = (DDVerticalLayout.VerticalLayoutTargetDetails) event
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
                ((DDVerticalLayout) comp).setDragMode(LayoutDragMode.CLONE);
                ((DDVerticalLayout) comp).setSpacing(true);
                ((DDVerticalLayout) comp).setDropHandler(DDVerticalLayoutDropHandler.this);
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
                ((DDHorizontalLayout) comp).setDropHandler(new DDHorizontalLayoutDropHandler());
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
            handleAttach(comp);
        } else {
            ((AbstractOrderedLayout) details.getTarget()).addComponent(transferable.getComponent());
        }
    }

    public void setStructure(ComponentStructure componentStructure) {
        this.componentStructure = componentStructure;
    }

    protected void handleAttach(Component newComponent) {
        componentStructure.addChild(newComponent);
        structureChangeListener.structureChanged();
    }

    public void addStructureChangeListener(StructureChangeListener structureChangeListener) {
        this.structureChangeListener = structureChangeListener;
    }
}
