/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.drop_handlers;

import com.audimex.dashboard.web.dashboard.tools.DropLayoutTools;
import com.audimex.dashboard.web.dashboard.vaadin_components.Draggable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.ui.Component;
import fi.jasoft.dragdroplayouts.DDGridLayout;
import fi.jasoft.dragdroplayouts.drophandlers.DefaultGridLayoutDropHandler;
import fi.jasoft.dragdroplayouts.events.LayoutBoundTransferable;

public class GridLayoutDropHandler extends DefaultGridLayoutDropHandler {
    protected DropLayoutTools tools;

    public GridLayoutDropHandler(DropLayoutTools tools) {
        this.tools = tools;
    }

    @Override
    protected void handleDropFromLayout(DragAndDropEvent event) {
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event
                .getTransferable();
        DDGridLayout.GridLayoutTargetDetails details = (DDGridLayout.GridLayoutTargetDetails) event
                .getTargetDetails();
        DDGridLayout layout = (DDGridLayout) details.getTarget();
        Component source = event.getTransferable().getSourceComponent();
        Component comp = transferable.getComponent();

        Component parent = layout.getParent();
        while (parent != null) {
            if (parent == comp) {
                return;
            }
            parent = parent.getParent();
        }

        if (comp instanceof Draggable) {
            int row = details.getOverRow();
            int column = details.getOverColumn();


            // If no components exist in the grid, then just add the
            // component
            if (!layout.getComponentIterator().hasNext()) {
                layout.addComponent(comp, column, row);
                return;
            }

            // If component was dropped on top of another component, abort
            if (layout.getComponent(column, row) != null) {
                return;
            }

            // Add the component

            tools.addComponent(layout, ((Draggable) comp).getLayout(), column, row);
            // Add component alignment if given

        }
    }

}
