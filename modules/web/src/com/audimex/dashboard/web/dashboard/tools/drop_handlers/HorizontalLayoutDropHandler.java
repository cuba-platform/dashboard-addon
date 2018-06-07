/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.tools.drop_handlers;

import com.audimex.dashboard.web.dashboard.tools.DropLayoutTools;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.shared.ui.dd.HorizontalDropLocation;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import fi.jasoft.dragdroplayouts.DDHorizontalLayout;
import fi.jasoft.dragdroplayouts.drophandlers.DefaultHorizontalLayoutDropHandler;
import fi.jasoft.dragdroplayouts.events.LayoutBoundTransferable;

import static com.vaadin.shared.ui.dd.HorizontalDropLocation.CENTER;
import static com.vaadin.shared.ui.dd.HorizontalDropLocation.RIGHT;

public class HorizontalLayoutDropHandler extends DefaultHorizontalLayoutDropHandler implements DropHandlerHelper {
    protected DropLayoutTools tools;

    public HorizontalLayoutDropHandler(DropLayoutTools tools) {
        this.tools = tools;
    }

    @Override
    protected void handleComponentReordering(DragAndDropEvent event) {
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
            return;
        }

        layout.removeComponent(comp);

        if (idx > oldIndex) {
            idx--;
        }

        HorizontalDropLocation loc = details.getDropLocation();
        if (loc == CENTER || loc == RIGHT) {
            idx++;
        }

        if (idx >= 0) {
            layout.addComponent(comp, idx);
        } else {
            layout.addComponent(comp, 0);
        }
    }

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

        HorizontalDropLocation loc = (details).getDropLocation();
        if (loc == CENTER || loc == RIGHT) {
            idx++;
        }

        addComponent(tools, layout, comp, idx);
    }


}

