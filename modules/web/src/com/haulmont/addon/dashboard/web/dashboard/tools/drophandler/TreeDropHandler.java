package com.haulmont.addon.dashboard.web.dashboard.tools.drophandler;

import com.haulmont.addon.dashboard.model.visualmodel.DashboardLayout;
import com.haulmont.addon.dashboard.model.visualmodel.GridLayout;
import com.haulmont.addon.dashboard.model.visualmodel.HorizontalLayout;
import com.haulmont.addon.dashboard.model.visualmodel.VerticalLayout;
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
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.ServerSideCriterion;
import com.vaadin.shared.ui.dd.VerticalDropLocation;

import java.util.UUID;

import static com.haulmont.addon.dashboard.web.dashboard.datasources.DashboardLayoutUtils.findLayout;
import static com.haulmont.addon.dashboard.web.dashboard.datasources.DashboardLayoutUtils.findParentLayout;

public class TreeDropHandler implements DropHandler {//todo

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
        UUID parentLayoutUuid = (UUID) targetDetails.getItemIdOver();

        VerticalDropLocation location = targetDetails.getDropLocation();

        if (sourceComponent instanceof DraggedComponentWrapper) {
            Component component = ((DraggedComponentWrapper) sourceComponent).getDraggedComponent(vTransferable);
            if (component instanceof CanvasLayout) {
                CanvasLayout canvasLayout = (CanvasLayout) component;
                DashboardLayout dashboardLayout = findLayout(dashboardLayoutTreeReadOnlyDs.getVisualModel(), canvasLayout.getUuid());
                events.publish(new WidgetMovedEvent(dashboardLayout, parentLayoutUuid, location));
            }

            if (component instanceof PaletteButton) {
                PaletteButton paletteButton = (PaletteButton) component;
                events.publish(new WidgetAddedEvent(paletteButton.getLayout(), parentLayoutUuid, location));
            }
        }

        if (sourceComponent instanceof CubaTree) {
            CubaTree cubaTree = (CubaTree) sourceComponent;
            DashboardLayout dashboardLayout = findLayout(dashboardLayoutTreeReadOnlyDs.getVisualModel(), (UUID) cubaTree.getValue());
            events.publish(new WidgetMovedEvent(dashboardLayout, parentLayoutUuid, location));
        }
    }

    @Override
    public AcceptCriterion getAcceptCriterion() {
        return new ServerSideCriterion() {
            @Override
            public boolean accept(DragAndDropEvent event) {
                Transferable vTransferable = event.getTransferable();
                com.vaadin.ui.Tree.TreeTargetDetails targetDetails =
                        (com.vaadin.ui.Tree.TreeTargetDetails) event.getTargetDetails();
                VerticalDropLocation location = targetDetails.getDropLocation();
                if (targetDetails.getTarget().getParent(targetDetails.getItemIdOver()) == null
                        && location != VerticalDropLocation.MIDDLE) {
                    return false;
                }
                if (targetDetails.getTarget().getParent(targetDetails.getItemIdAfter()) == null
                        && location != VerticalDropLocation.MIDDLE) {
                    return false;
                }
                UUID parentLayoutUuid = (UUID) targetDetails.getItemIdOver();
                DashboardLayout dashboardLayout = findLayout(dashboardLayoutTreeReadOnlyDs.getVisualModel(), parentLayoutUuid);
                com.vaadin.ui.Component sourceComponent = vTransferable.getSourceComponent();

                DashboardLayout sourceLayout = null;
                if (sourceComponent instanceof DraggedComponentWrapper) {
                    Component component = ((DraggedComponentWrapper) sourceComponent).getDraggedComponent(vTransferable);
                    if (component instanceof CanvasLayout) {
                        CanvasLayout canvasLayout = (CanvasLayout) component;
                        sourceLayout = findLayout(dashboardLayoutTreeReadOnlyDs.getVisualModel(), canvasLayout.getUuid());

                    }
                }

                if (sourceComponent instanceof CubaTree) {
                    CubaTree cubaTree = (CubaTree) sourceComponent;
                    sourceLayout = findLayout(dashboardLayoutTreeReadOnlyDs.getVisualModel(), (UUID) cubaTree.getValue());
                }

                if (sourceLayout != null) {

                    if (sourceLayout.isRoot()) {
                        return false;
                    }

                    DashboardLayout parent = findParentLayout(dashboardLayoutTreeReadOnlyDs.getVisualModel(), sourceLayout);
                    if (parent instanceof GridLayout) {
                        return false;
                    }
                }

                return location != VerticalDropLocation.MIDDLE
                        || dashboardLayout instanceof VerticalLayout
                        || dashboardLayout instanceof HorizontalLayout;
            }
        };
    }
}