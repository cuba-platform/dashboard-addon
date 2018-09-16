package com.haulmont.addon.dashboard.web.dashboard.tools.drophandler;

import com.haulmont.addon.dashboard.model.visualmodel.DashboardLayout;
import com.haulmont.addon.dashboard.web.dashboard.datasources.DashboardLayoutTreeReadOnlyDs;
import com.haulmont.addon.dashboard.web.dashboard.events.WidgetAddedEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.WidgetMovedEvent;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.components.PaletteButton;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasLayout;
import com.haulmont.addon.dnd.web.gui.components.DraggedComponentWrapper;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.web.toolkit.ui.CubaTree;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.shared.ui.dd.VerticalDropLocation;

import java.util.UUID;

import static com.haulmont.addon.dashboard.utils.DashboardLayoutUtils.findLayout;

public class TreeDropHandler implements DropHandler {

    private Events events = AppBeans.get(Events.class);

    private DashboardLayoutTreeReadOnlyDs dashboardLayoutTreeReadOnlyDs;

    public TreeDropHandler(DashboardLayoutTreeReadOnlyDs dashboardLayoutTreeReadOnlyDs) {
        this.dashboardLayoutTreeReadOnlyDs = dashboardLayoutTreeReadOnlyDs;
    }

    @Override
    public void drop(DragAndDropEvent event) {
        Transferable vTransferable = event.getTransferable();
        com.vaadin.ui.Tree.TreeTargetDetails targetDetails =
                (com.vaadin.ui.Tree.TreeTargetDetails) event.getTargetDetails();
        com.vaadin.ui.Component sourceComponent = vTransferable.getSourceComponent();
        UUID targetLayoutUuid = (UUID) targetDetails.getItemIdOver();

        VerticalDropLocation location = targetDetails.getDropLocation();

        if (vTransferable instanceof DataBoundTransferable && sourceComponent == null) {
            DashboardLayout dashboardLayout = findLayout(dashboardLayoutTreeReadOnlyDs.getVisualModel(),
                    (UUID) ((DataBoundTransferable) vTransferable).getItemId());
            events.publish(new WidgetMovedEvent(dashboardLayout, (UUID) targetDetails.getItemIdOver(), location.name()));
            return;
        }

        if (sourceComponent instanceof DraggedComponentWrapper) {
            Component component = ((DraggedComponentWrapper) sourceComponent).getDraggedComponent(vTransferable);
            if (component instanceof CanvasLayout) {
                CanvasLayout canvasLayout = (CanvasLayout) component;
                DashboardLayout dashboardLayout = findLayout(dashboardLayoutTreeReadOnlyDs.getVisualModel(), canvasLayout.getUuid());
                events.publish(new WidgetMovedEvent(dashboardLayout, targetLayoutUuid, location.name()));
            }

            if (component instanceof PaletteButton) {
                PaletteButton paletteButton = (PaletteButton) component;
                events.publish(new WidgetAddedEvent(paletteButton.getLayout(), targetLayoutUuid, location.name()));
            }
        }

        if (sourceComponent instanceof CubaTree) {
            CubaTree cubaTree = (CubaTree) sourceComponent;
            DashboardLayout dashboardLayout = findLayout(dashboardLayoutTreeReadOnlyDs.getVisualModel(), (UUID) cubaTree.getValue());
            events.publish(new WidgetMovedEvent(dashboardLayout, targetLayoutUuid, location.name()));
        }
    }

    @Override
    public AcceptCriterion getAcceptCriterion() {
        return AcceptAll.get();
    }
}
