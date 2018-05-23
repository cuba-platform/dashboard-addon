/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.drop_handlers;

import com.audimex.dashboard.web.dashboard.tools.DropLayoutTools;
import com.audimex.dashboard.web.dashboard.vaadin_components.Draggable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.shared.ui.dd.HorizontalDropLocation;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import fi.jasoft.dragdroplayouts.DDHorizontalLayout;
import fi.jasoft.dragdroplayouts.drophandlers.DefaultHorizontalLayoutDropHandler;
import fi.jasoft.dragdroplayouts.events.LayoutBoundTransferable;

public class HorizontalLayoutDropHandler extends DefaultHorizontalLayoutDropHandler {
    protected DropLayoutTools tools;

    public HorizontalLayoutDropHandler(DropLayoutTools tools) {
        this.tools = tools;
    }

    //deleted only alignment processing
    @Override
    protected void handleComponentReordering(DragAndDropEvent event) {
        // Component re-ordering
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event
                .getTransferable();
        DDHorizontalLayout.HorizontalLayoutTargetDetails details = (DDHorizontalLayout.HorizontalLayoutTargetDetails) event
                .getTargetDetails();
        AbstractOrderedLayout layout = (AbstractOrderedLayout) details
                .getTarget();
        Component comp = transferable.getComponent();
        int idx = details.getOverIndex();
        int oldIndex = layout.getComponentIndex(comp);

        if (idx == oldIndex) {
            // Index did not change
            return;
        }

        // Detach
        layout.removeComponent(comp);

        // Account for detachment if new index is bigger then old index
        if (idx > oldIndex) {
            idx--;
        }

        // Increase index if component is dropped after or above a previous
        // component
        HorizontalDropLocation loc = details.getDropLocation();
        if (loc == HorizontalDropLocation.CENTER
                || loc == HorizontalDropLocation.RIGHT) {
            idx++;
        }

        // Add component
        if (idx >= 0) {
            layout.addComponent(comp, idx);
        } else {
            layout.addComponent(comp, 0);
        }

        // Add component alignment if given
    }

    //deleted alignment processing, added check Draggable
    @Override
    protected void handleDropFromLayout(DragAndDropEvent event) {
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event
                .getTransferable();
        DDHorizontalLayout.HorizontalLayoutTargetDetails details = (DDHorizontalLayout.HorizontalLayoutTargetDetails) event
                .getTargetDetails();
        AbstractOrderedLayout layout = (AbstractOrderedLayout) details
                .getTarget();
        int idx = (details).getOverIndex();
        Component comp = transferable.getComponent();

        Component parent = layout.getParent();
        while (parent != null) {
            if (parent == comp) {
                return;
            }
            parent = parent.getParent();
        }

        if (comp instanceof Draggable) {
            HorizontalDropLocation loc = (details).getDropLocation();
            if (loc == HorizontalDropLocation.CENTER
                    || loc == HorizontalDropLocation.RIGHT) {
                idx++;
            }

            // Add component
            if (idx >= 0) {
                tools.addComponent(layout, ((Draggable) comp).getLayout(), idx);
            } else {
                tools.addComponent(layout, ((Draggable) comp).getLayout());
            }
        }

        // Add component alignment if given
    }


}

