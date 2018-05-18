/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.drop_handlers;

import com.audimex.dashboard.gui.Draggable;
import com.audimex.dashboard.model.visual_model.DashboardLayout;
import com.audimex.dashboard.web.dashboard.tools.DropLayoutTool;
import com.haulmont.addon.dnd.components.DDHorizontalLayout;
import com.haulmont.addon.dnd.components.DDHorizontalLayoutTargetDetails;
import com.haulmont.addon.dnd.components.LayoutBoundTransferable;
import com.haulmont.addon.dnd.components.dragevent.DragAndDropEvent;
import com.haulmont.addon.dnd.components.dragevent.Transferable;
import com.haulmont.addon.dnd.components.enums.HorizontalDropLocation;
import com.haulmont.cuba.gui.components.Component;

public class HorizontalLayoutDropHandler extends AbstractLayoutDropHandler {

    public HorizontalLayoutDropHandler(DropLayoutTool tool) {
        super(tool);
    }

    @Override
    public void drop(DragAndDropEvent event) {
        DDHorizontalLayoutTargetDetails details = (DDHorizontalLayoutTargetDetails) event.getTargetDetails();
        Transferable t = event.getTransferable();

        DDHorizontalLayout targetLayout = (DDHorizontalLayout) details.getTarget();
        Component component = ((LayoutBoundTransferable) t).getTransferableComponent();
        Component sourceLayout = t.getSourceComponent();
        int indexTo = details.getOverIndex();
        int indexFrom = targetLayout.indexOf(component);

        if (component == null) {
            return;
        }

        Component parent = targetLayout;
        while (parent != null) {
            if (parent == component) {
                return;
            }
            parent = parent.getParent();
        }

        if (sourceLayout == targetLayout) {
            if (indexFrom == indexTo) {
                return;
            }
            targetLayout.remove(component);
            if (indexTo > indexFrom) {
                indexTo--;
            }
            HorizontalDropLocation loc = details.getDropLocation();
            if (loc == HorizontalDropLocation.CENTER
                    || loc == HorizontalDropLocation.RIGHT) {
                indexTo++;
            }
            if (indexTo >= 0) {
                targetLayout.add(component, indexTo);
            } else {
                targetLayout.add(component);
            }
        } else if (component instanceof Draggable) {
            HorizontalDropLocation loc = details.getDropLocation();
            if (loc == HorizontalDropLocation.CENTER
                    || loc == HorizontalDropLocation.RIGHT) {
                indexTo++;
            }
            DashboardLayout layout = ((Draggable) component).getLayout();

            if (indexTo >= 0) {
                tool.addComponent(targetLayout, layout, indexTo);
            } else {
                tool.addComponent(targetLayout, layout);
            }
        }
    }
}

