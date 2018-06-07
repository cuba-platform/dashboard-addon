/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.tools.drop_handlers;

import com.audimex.dashboard.web.dashboard.tools.DropLayoutTools;
import com.audimex.dashboard.web.dashboard.frames.editor.vaadin_components.Draggable;
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
        Component comp = transferable.getComponent();

        if (comp instanceof Draggable) {
            int row = details.getOverRow();
            int column = details.getOverColumn();

            if (!layout.iterator().hasNext()) {
                layout.addComponent(comp, column, row);
                return;
            }

            if (layout.getComponent(column, row) != null) {
                return;
            }

            tools.addComponent(layout, ((Draggable) comp).getLayout(), column, row);
        }
    }
}
