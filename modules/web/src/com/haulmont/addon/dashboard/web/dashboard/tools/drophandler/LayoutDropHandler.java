package com.haulmont.addon.dashboard.web.dashboard.tools.drophandler;

import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.visualmodel.DashboardLayout;
import com.haulmont.addon.dashboard.web.dashboard.events.WidgetAddedEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.WidgetMovedEvent;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.components.Draggable;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.components.PaletteButton;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasLayout;
import com.haulmont.addon.dnd.components.DDHorizontalLayoutTargetDetails;
import com.haulmont.addon.dnd.components.DDVerticalLayoutTargetDetails;
import com.haulmont.addon.dnd.components.DropHandler;
import com.haulmont.addon.dnd.components.LayoutBoundTransferable;
import com.haulmont.addon.dnd.components.acceptcriterion.AcceptCriterion;
import com.haulmont.addon.dnd.components.dragevent.DragAndDropEvent;
import com.haulmont.addon.dnd.components.dragevent.TargetDetails;
import com.haulmont.addon.dnd.web.gui.components.AcceptCriterionWrapper;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.web.toolkit.ui.CubaTree;
import com.vaadin.event.dd.acceptcriteria.ServerSideCriterion;

import static com.haulmont.addon.dashboard.utils.DashboardLayoutUtils.findLayout;

public class LayoutDropHandler implements DropHandler {
    protected Dashboard dashboard;
    private Events events = AppBeans.get(Events.class);

    public LayoutDropHandler(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    @Override
    public void drop(DragAndDropEvent event) {
        String location = null;
        CanvasLayout targetCanvasLayout = null;
        if (event.getTargetDetails() instanceof DDVerticalLayoutTargetDetails) {
            DDVerticalLayoutTargetDetails vDetails = (DDVerticalLayoutTargetDetails) event.getTargetDetails();
            location = vDetails.getDropLocation().name();
            targetCanvasLayout = (CanvasLayout) vDetails.getOverComponent();
        } else if (event.getTargetDetails() instanceof DDHorizontalLayoutTargetDetails) {
            DDHorizontalLayoutTargetDetails hDetails = (DDHorizontalLayoutTargetDetails) event.getTargetDetails();
            location = hDetails.getDropLocation().name();
            targetCanvasLayout = (CanvasLayout) hDetails.getOverComponent();
        }

        if (targetCanvasLayout == null) {
            TargetDetails details = event.getTargetDetails();
            Component.OrderedContainer targetLayout = (Component.OrderedContainer) details.getTarget();
            targetCanvasLayout = (CanvasLayout) targetLayout.getParent();
        }

        LayoutBoundTransferable t = (LayoutBoundTransferable) event.getTransferable();
        Component component = t.getTransferableComponent();

        if (component instanceof Draggable) {
            PaletteButton paletteButton = (PaletteButton) component;
            events.publish(new WidgetAddedEvent(paletteButton.getLayout(), targetCanvasLayout.getUuid(), location));
        }
        if (component instanceof CanvasLayout && component.getParent() instanceof Component.OrderedContainer) {
            Component.OrderedContainer parent = (Component.OrderedContainer) component.getParent();
            if (parent != null) {
                DashboardLayout dashboardLayout = findLayout(dashboard.getVisualModel(), ((CanvasLayout) component).getUuid());
                events.publish(new WidgetMovedEvent(dashboardLayout, targetCanvasLayout.getUuid(), location));
            }
        }
        if (component instanceof CanvasLayout && component.getParent() == null) {
            DashboardLayout dashboardLayout = findLayout(dashboard.getVisualModel(), ((CanvasLayout) component).getUuid());
            events.publish(new WidgetMovedEvent(dashboardLayout, targetCanvasLayout.getUuid(), location));
        }

    }

    @Override
    public AcceptCriterion getCriterion() {
        return (AcceptCriterionWrapper) () -> new ServerSideCriterion() {
            @Override
            public boolean accept(com.vaadin.event.dd.DragAndDropEvent event) {
                com.vaadin.ui.Component component = event.getTransferable().getSourceComponent();
                if (component instanceof CubaTree) {
                    return false;
                }
                return true;
            }
        };
    }
}
