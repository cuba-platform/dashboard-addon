/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.drop_handlers;

import com.audimex.dashboard.gui.Draggable;
import com.haulmont.addon.dnd.components.DDHorizontalLayout;
import com.haulmont.addon.dnd.components.DDHorizontalLayoutTargetDetails;
import com.haulmont.addon.dnd.components.DropHandler;
import com.haulmont.addon.dnd.components.LayoutBoundTransferable;
import com.haulmont.addon.dnd.components.acceptcriterion.AcceptCriterion;
import com.haulmont.addon.dnd.components.dragevent.DragAndDropEvent;
import com.haulmont.addon.dnd.components.dragevent.Transferable;
import com.haulmont.addon.dnd.components.enums.HorizontalDropLocation;
import com.haulmont.cuba.gui.components.Component;
import org.springframework.context.annotation.Scope;

@org.springframework.stereotype.Component
@Scope("prototype")
public abstract class HorizontalLayoutDropHandler implements DropHandler {

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
            component = getComponent((Draggable) component);

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
        }
    }

    @Override
    public AcceptCriterion getCriterion() {
        return AcceptCriterion.ACCEPT_ALL;
    }

    public abstract Component getComponent(Draggable component);
}
