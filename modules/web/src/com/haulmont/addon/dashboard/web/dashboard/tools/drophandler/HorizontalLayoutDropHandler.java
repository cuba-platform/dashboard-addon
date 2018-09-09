/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.tools.drophandler;

import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasLayout;
import com.haulmont.addon.dashboard.web.dashboard.tools.DropLayoutTools;
import com.haulmont.addon.dnd.components.DDHorizontalLayout;
import com.haulmont.addon.dnd.components.DDHorizontalLayoutTargetDetails;
import com.haulmont.addon.dnd.components.LayoutBoundTransferable;
import com.haulmont.addon.dnd.components.defaulthandlers.DefaultHorizontalDropHandler;
import com.haulmont.addon.dnd.components.dragevent.DragAndDropEvent;
import com.haulmont.addon.dnd.components.enums.HorizontalDropLocation;
import com.haulmont.cuba.gui.components.Component;

import static com.haulmont.addon.dnd.components.enums.HorizontalDropLocation.CENTER;
import static com.haulmont.addon.dnd.components.enums.HorizontalDropLocation.RIGHT;

public class HorizontalLayoutDropHandler extends DefaultHorizontalDropHandler {
    protected DropLayoutTools tools;

    public HorizontalLayoutDropHandler(DropLayoutTools tools) {
        this.tools = tools;
    }

    @Override
    public void drop(DragAndDropEvent event) {
        DDHorizontalLayoutTargetDetails details = (DDHorizontalLayoutTargetDetails) event.getTargetDetails();
        LayoutBoundTransferable t = (LayoutBoundTransferable) event.getTransferable();

        DDHorizontalLayout targetLayout = (DDHorizontalLayout) details.getTarget();
        Component component = t.getTransferableComponent();
        Component sourceLayout = t.getSourceComponent();

        int indexTo = details.getOverIndex();
        int indexFrom = targetLayout.indexOf(component);

        if (component == null) {
            return;
        }

        if (targetLayout.getParent() instanceof CanvasLayout && component instanceof CanvasLayout) {
            CanvasLayout targetCanvasLayout = (CanvasLayout) targetLayout.getParent();
            CanvasLayout sourceCanvasLayout = (CanvasLayout) component;
            if (targetCanvasLayout.getUuid().equals(sourceCanvasLayout.getUuid())) {
                return;
            }
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
            if (loc == CENTER || loc == RIGHT) {
                indexTo++;
            }

            if (indexTo >= 0) {
                tools.addCanvasComponent(targetLayout, component, indexTo);
            } else {
                tools.addCanvasComponent(targetLayout, component, 0);
            }
        } else {
            HorizontalDropLocation loc = details.getDropLocation();
            if (loc == CENTER || loc == RIGHT) {
                indexTo++;
            }

            tools.addCanvasComponent(targetLayout, component, indexTo);
        }
    }

}

