package com.haulmont.addon.dashboard.web.dashboard.tools.drophandler;

import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.visualmodel.DashboardLayout;
import com.haulmont.addon.dashboard.web.dashboard.events.WidgetAddedEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.WidgetMovedEvent;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.components.Draggable;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.components.PaletteButton;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasLayout;
import com.haulmont.addon.dnd.components.*;
import com.haulmont.addon.dnd.components.acceptcriterion.AcceptCriterion;
import com.haulmont.addon.dnd.components.dragevent.DragAndDropEvent;
import com.haulmont.addon.dnd.components.dragevent.DropTarget;
import com.haulmont.addon.dnd.components.dragevent.TargetDetails;
import com.haulmont.addon.dnd.web.gui.components.AcceptCriterionWrapper;
import com.haulmont.addon.dnd.web.gui.components.WebDragAndDropWrapper;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.web.gui.components.WebTextArea;
import com.haulmont.cuba.web.toolkit.ui.CubaTree;
import com.vaadin.event.dd.acceptcriteria.ServerSideCriterion;
import org.strangeway.responsive.web.components.ResponsiveLayout;
import org.strangeway.responsive.web.components.impl.WebResponsiveColumn;
import org.strangeway.responsive.web.components.impl.WebResponsiveRow;

import java.util.Iterator;

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
        } else if (event.getTargetDetails() instanceof DDCssLayoutTargetDetails) {
            DDCssLayoutTargetDetails cssLayoutTargetDetails = (DDCssLayoutTargetDetails) event.getTargetDetails();
            if (cssLayoutTargetDetails.getHorizontalDropLocation() != null) {
                location = cssLayoutTargetDetails.getHorizontalDropLocation().name();
            }
            if (location == null && cssLayoutTargetDetails.getVerticalDropLocation() != null) {
                location = cssLayoutTargetDetails.getVerticalDropLocation().name();
            }
            targetCanvasLayout = (CanvasLayout) cssLayoutTargetDetails.getOverComponent();
        } else if (event.getTargetDetails() instanceof DragAndDropWrapperTargetDetails) {
            DragAndDropWrapperTargetDetails wDetails = (DragAndDropWrapperTargetDetails) event.getTargetDetails();
            WebDragAndDropWrapper dt = (WebDragAndDropWrapper) wDetails.getTarget();
            ResponsiveLayout rl = (ResponsiveLayout)dt.getDraggedComponent();

            Iterator it = rl.getOwnComponents().iterator();
            WebResponsiveRow wrr = (WebResponsiveRow) it.next();

            WebResponsiveColumn wrc = new WebResponsiveColumn();
            wrc.add(new WebTextArea());
            wrr.addColumn(wrc);

            WebResponsiveColumn wrc2 = new WebResponsiveColumn();
            wrc2.add(new WebTextArea());
            wrr.addColumn(wrc2);

            WebResponsiveColumn wrc3 = new WebResponsiveColumn();
            wrc3.add(new WebTextArea());
            wrr.addColumn(wrc3);

            return;

        }

        if (targetCanvasLayout == null) {
            TargetDetails details = event.getTargetDetails();
            Component.OrderedContainer targetLayout = (Component.OrderedContainer) details.getTarget();
            targetCanvasLayout = findParentCanvas(targetLayout);
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

    private CanvasLayout findParentCanvas(Component.OrderedContainer targetLayout) {
        if (targetLayout.getParent() instanceof CanvasLayout) {
            return (CanvasLayout) targetLayout.getParent();
        } else if (targetLayout.getParent() instanceof Component.OrderedContainer) {
            return findParentCanvas((Component.OrderedContainer) targetLayout.getParent());
        }
        return null;
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
