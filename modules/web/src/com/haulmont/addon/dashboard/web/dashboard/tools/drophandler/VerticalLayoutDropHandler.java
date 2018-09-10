/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.tools.drophandler;

import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasLayout;
import com.haulmont.addon.dashboard.web.dashboard.tools.DropLayoutTools;
import com.haulmont.addon.dnd.components.DDVerticalLayout;
import com.haulmont.addon.dnd.components.DDVerticalLayoutTargetDetails;
import com.haulmont.addon.dnd.components.LayoutBoundTransferable;
import com.haulmont.addon.dnd.components.defaulthandlers.DefaultVerticalDropHandler;
import com.haulmont.addon.dnd.components.dragevent.DragAndDropEvent;
import com.haulmont.addon.dnd.components.enums.VerticalDropLocation;
import com.haulmont.cuba.gui.components.Component;

import static com.haulmont.addon.dnd.components.enums.VerticalDropLocation.BOTTOM;
import static com.haulmont.addon.dnd.components.enums.VerticalDropLocation.MIDDLE;

public class VerticalLayoutDropHandler extends DefaultVerticalDropHandler {

    protected DropLayoutTools tools;

    public VerticalLayoutDropHandler(DropLayoutTools tools) {//TODO добавление конкретного типа разметки
        this.tools = tools;
    }

    @Override
    public void drop(DragAndDropEvent event) {
        DDVerticalLayoutTargetDetails details = (DDVerticalLayoutTargetDetails) event.getTargetDetails();
        LayoutBoundTransferable t = (LayoutBoundTransferable) event.getTransferable();

        Component component = t.getTransferableComponent();
        DDVerticalLayout targetLayout = (DDVerticalLayout) details.getTarget();
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

            VerticalDropLocation loc = details.getDropLocation();
            if (loc == MIDDLE || loc == BOTTOM) {
                indexTo++;
            }

            if (indexTo >= 0) {
                //tools.addCanvasComponent(targetLayout, component, indexTo);
            } else {
                //tools.addCanvasComponent(targetLayout, component, 0);
            }

        } else {
            VerticalDropLocation loc = details.getDropLocation();
            if (loc == MIDDLE || loc == BOTTOM) {
                indexTo++;
            }

            //tools.addCanvasComponent(targetLayout, component, indexTo);
        }

    }

}
