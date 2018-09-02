package com.haulmont.addon.dashboard.web.dashboard.tools.drop_handlers;

import com.haulmont.addon.dashboard.model.visual_model.DashboardLayout;
import com.haulmont.addon.dashboard.model.visual_model.HorizontalLayout;
import com.haulmont.addon.dashboard.model.visual_model.VerticalLayout;
import com.haulmont.addon.dashboard.web.dashboard.datasources.DashboardLayoutTreeReadOnlyDs;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.vaadin_components.PaletteButton;
import com.haulmont.addon.dashboard.web.events.LayoutAddedToTreeEvent;
import com.haulmont.addon.dnd.web.gui.components.DraggedComponentWrapper;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.gui.components.Component;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.ServerSideCriterion;
import com.vaadin.ui.Tree;

import java.util.UUID;

import static com.haulmont.addon.dashboard.web.dashboard.datasources.DashboardLayoutUtils.findLayout;

public class TreeDropHandler implements DropHandler {

    private DashboardLayoutTreeReadOnlyDs dashboardLayoutTreeReadOnlyDs;

    public TreeDropHandler(DashboardLayoutTreeReadOnlyDs dashboardLayoutTreeReadOnlyDs) {
        this.dashboardLayoutTreeReadOnlyDs = dashboardLayoutTreeReadOnlyDs;
    }

    @Override
    public void drop(DragAndDropEvent event) {
        Transferable vTransferable = event.getTransferable();
        Component component = ((DraggedComponentWrapper) vTransferable.getSourceComponent()).getDraggedComponent(vTransferable);
        if (component instanceof PaletteButton) {
            PaletteButton paletteButton = (PaletteButton) component;
            UUID parentLayoutUuid = (UUID) ((Tree.TreeTargetDetails) event.getTargetDetails()).getItemIdOver();
            Events events = AppBeans.get(Events.class);
            events.publish(new LayoutAddedToTreeEvent(new LayoutAddedToTreeEvent.LayoutAddedToTree(parentLayoutUuid, paletteButton.getLayout())));

        }
    }

    @Override
    public AcceptCriterion getAcceptCriterion() {
        return new ServerSideCriterion() {
            @Override
            public boolean accept(DragAndDropEvent event) {
                Transferable vTransferable = event.getTransferable();
                Component component = ((DraggedComponentWrapper) vTransferable.getSourceComponent()).getDraggedComponent(vTransferable);
                if (component instanceof PaletteButton) {
                    UUID parentLayoutUuid = (UUID) ((Tree.TreeTargetDetails) event.getTargetDetails()).getItemIdOver();
                    DashboardLayout dashboardLayout = findLayout(dashboardLayoutTreeReadOnlyDs.getVisualModel(), parentLayoutUuid);
                    if (dashboardLayout instanceof VerticalLayout || dashboardLayout instanceof HorizontalLayout) {
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }
        };
    }
}
