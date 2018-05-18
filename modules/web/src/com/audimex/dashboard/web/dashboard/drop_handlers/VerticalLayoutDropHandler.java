package com.audimex.dashboard.web.dashboard.drop_handlers;

import com.audimex.dashboard.gui.Draggable;
import com.audimex.dashboard.model.visual_model.DashboardLayout;
import com.audimex.dashboard.web.dashboard.tools.DropLayoutTool;
import com.haulmont.addon.dnd.components.DDVerticalLayout;
import com.haulmont.addon.dnd.components.DDVerticalLayoutTargetDetails;
import com.haulmont.addon.dnd.components.LayoutBoundTransferable;
import com.haulmont.addon.dnd.components.dragevent.DragAndDropEvent;
import com.haulmont.addon.dnd.components.enums.VerticalDropLocation;
import com.haulmont.cuba.gui.components.Component;

public class VerticalLayoutDropHandler extends AbstractLayoutDropHandler {

    public VerticalLayoutDropHandler(DropLayoutTool tool) {
        super(tool);
    }

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
            VerticalDropLocation loc = details.getDropLocation();
            if (loc == VerticalDropLocation.MIDDLE
                    || loc == VerticalDropLocation.BOTTOM) {
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
