package com.audimex.dashboard.web.dashboard.drop_handlers;

import com.audimex.dashboard.gui.Draggable;
import com.haulmont.addon.dnd.components.DDVerticalLayout;
import com.haulmont.addon.dnd.components.DDVerticalLayoutTargetDetails;
import com.haulmont.addon.dnd.components.DropHandler;
import com.haulmont.addon.dnd.components.LayoutBoundTransferable;
import com.haulmont.addon.dnd.components.acceptcriterion.AcceptCriterion;
import com.haulmont.addon.dnd.components.dragevent.DragAndDropEvent;
import com.haulmont.addon.dnd.components.enums.VerticalDropLocation;
import com.haulmont.cuba.gui.components.Component;
import org.springframework.context.annotation.Scope;

@org.springframework.stereotype.Component
@Scope("prototype")
public abstract class VerticalLayoutDropHandler implements DropHandler {

    @Override
    public void drop(DragAndDropEvent event) {
        DDVerticalLayoutTargetDetails details = (DDVerticalLayoutTargetDetails) event.getTargetDetails();
        LayoutBoundTransferable t = (LayoutBoundTransferable) event.getTransferable();

        Component component = t.getTransferableComponent();
        DDVerticalLayout targetLayout = (DDVerticalLayout) details.getTarget();

        Component sourceLayout = t.getSourceComponent();

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

        int indexTo = details.getOverIndex();
        int indexFrom = targetLayout.indexOf(component);

        if (sourceLayout == targetLayout) {
            if (indexFrom == indexTo) {
                return;
            }
            targetLayout.remove(component);
            if (indexTo > indexFrom) {
                indexTo--;
            }
            VerticalDropLocation loc = details.getDropLocation();
            if (loc == VerticalDropLocation.MIDDLE
                    || loc == VerticalDropLocation.BOTTOM) {
                indexTo++;
            }
            if (indexTo >= 0) {
                targetLayout.add(component, indexTo);
            } else {
                targetLayout.add(component);
            }
        } else if (component instanceof Draggable) {
            component = getComponent((Draggable) component);

            VerticalDropLocation loc = details.getDropLocation();
            if (loc == VerticalDropLocation.MIDDLE
                    || loc == VerticalDropLocation.BOTTOM) {
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
