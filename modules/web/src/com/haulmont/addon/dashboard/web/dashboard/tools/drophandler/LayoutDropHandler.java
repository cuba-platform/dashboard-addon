package com.haulmont.addon.dashboard.web.dashboard.tools.drophandler;

import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.visualmodel.DashboardLayout;
import com.haulmont.addon.dashboard.web.dashboard.events.WidgetAddedEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.WidgetMovedEvent;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.components.Draggable;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.components.PaletteButton;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasLayout;
import com.haulmont.addon.dashboard.web.dashboard.tools.DropLayoutTools;
import com.haulmont.addon.dnd.components.DDHorizontalLayoutTargetDetails;
import com.haulmont.addon.dnd.components.DDVerticalLayoutTargetDetails;
import com.haulmont.addon.dnd.components.DropHandler;
import com.haulmont.addon.dnd.components.LayoutBoundTransferable;
import com.haulmont.addon.dnd.components.acceptcriterion.AcceptCriterion;
import com.haulmont.addon.dnd.components.acceptcriterion.ServerSideCriterion;
import com.haulmont.addon.dnd.components.dragevent.DragAndDropEvent;
import com.haulmont.addon.dnd.components.dragevent.TargetDetails;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.gui.components.Component;

import static com.haulmont.addon.dashboard.utils.DashboardLayoutUtils.findLayout;
import static com.haulmont.addon.dnd.components.enums.HorizontalDropLocation.CENTER;
import static com.haulmont.addon.dnd.components.enums.HorizontalDropLocation.RIGHT;
import static com.haulmont.addon.dnd.components.enums.VerticalDropLocation.BOTTOM;
import static com.haulmont.addon.dnd.components.enums.VerticalDropLocation.MIDDLE;

public class LayoutDropHandler implements DropHandler {
    protected Dashboard dashboard;
    private Events events = AppBeans.get(Events.class);

    public LayoutDropHandler(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    @Override
    public void drop(DragAndDropEvent event) {
        String location = null;
        int indexTo = 0;
        if (event.getTargetDetails() instanceof DDVerticalLayoutTargetDetails) {
            DDVerticalLayoutTargetDetails vDetails = (DDVerticalLayoutTargetDetails) event.getTargetDetails();
            location = vDetails.getDropLocation().name();
            indexTo = vDetails.getOverIndex();
            if (vDetails.getDropLocation() == MIDDLE || vDetails.getDropLocation() == BOTTOM) {
                indexTo++;
            }
        } else if (event.getTargetDetails() instanceof DDHorizontalLayoutTargetDetails) {
            DDHorizontalLayoutTargetDetails hDetails = (DDHorizontalLayoutTargetDetails) event.getTargetDetails();
            location = hDetails.getDropLocation().name();
            indexTo = hDetails.getOverIndex();
            if (hDetails.getDropLocation() == CENTER || hDetails.getDropLocation() == RIGHT) {
                indexTo++;
            }
        }

        TargetDetails details = event.getTargetDetails();
        LayoutBoundTransferable t = (LayoutBoundTransferable) event.getTransferable();
        Component.OrderedContainer targetLayout = (Component.OrderedContainer) details.getTarget();
        Component component = t.getTransferableComponent();
        int indexFrom = targetLayout.indexOf(component);
        if (indexTo > indexFrom) {
            indexTo--;
        }

        CanvasLayout targetCanvasLayout = (CanvasLayout) targetLayout.getParent();
        //CanvasLayout sourceCanvasLayout = (CanvasLayout) component.getParent();

        if (component instanceof Draggable) {
            PaletteButton paletteButton = (PaletteButton) component;
            events.publish(new WidgetAddedEvent(paletteButton.getLayout(), targetCanvasLayout.getUuid(), location, indexTo));
        }
        if (component instanceof CanvasLayout && component.getParent() instanceof Component.OrderedContainer) {
            Component.OrderedContainer parent = (Component.OrderedContainer) component.getParent();
            if (parent != null) {
                DashboardLayout dashboardLayout = findLayout(dashboard.getVisualModel(), ((CanvasLayout) component).getUuid());
                events.publish(new WidgetMovedEvent(dashboardLayout, targetCanvasLayout.getUuid(), location, indexTo));
            }
        }
        if (component instanceof CanvasLayout && component.getParent() == null) {
            DashboardLayout dashboardLayout = findLayout(dashboard.getVisualModel(), ((CanvasLayout) component).getUuid());
            events.publish(new WidgetMovedEvent(dashboardLayout, targetCanvasLayout.getUuid(), location, indexTo));
        }

    }

    @Override
    public AcceptCriterion getCriterion() {
        return new ServerSideCriterion() {
            @Override
            public boolean accept(DragAndDropEvent event) {
                TargetDetails details = event.getTargetDetails();
                LayoutBoundTransferable t = (LayoutBoundTransferable) event.getTransferable();

                Component targetLayout = details.getTarget();
                Component component = t.getTransferableComponent();

                if (targetLayout.getParent() instanceof CanvasLayout && component instanceof CanvasLayout) {
                    CanvasLayout targetCanvasLayout = (CanvasLayout) targetLayout.getParent();
                    CanvasLayout sourceCanvasLayout = (CanvasLayout) component;
                    if (targetCanvasLayout.getUuid().equals(sourceCanvasLayout.getUuid())) {
                        return false;
                    }
                }
                return true;
            }
        };
    }
}
