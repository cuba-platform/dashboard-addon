/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.drop_handlers;

import com.audimex.dashboard.gui.Draggable;
import com.audimex.dashboard.model.visual_model.DashboardLayout;
import com.audimex.dashboard.web.dashboard.tools.DropLayoutTool;
import com.haulmont.addon.dnd.components.DDGridLayout;
import com.haulmont.addon.dnd.components.DDGridLayoutTargetDetails;
import com.haulmont.addon.dnd.components.LayoutBoundTransferable;
import com.haulmont.addon.dnd.components.dragevent.DragAndDropEvent;
import com.haulmont.cuba.gui.components.Component;

public class GridLayoutDropHandler extends AbstractLayoutDropHandler {

    public GridLayoutDropHandler(DropLayoutTool tool) {
        super(tool);
    }

    @Override
    public void drop(DragAndDropEvent event) {
        DDGridLayoutTargetDetails details = (DDGridLayoutTargetDetails) event.getTargetDetails();
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event.getTransferable();
        Component component = transferable.getTransferableComponent();
        Component sourceLayout = transferable.getSourceComponent();
        Component over = details.getOverComponent();
        DDGridLayout targetLayout = (DDGridLayout) details.getTarget();
        int row = details.getOverRow();
        int col = details.getOverColumn();

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

        if (over == null && component instanceof Draggable) {
            DashboardLayout layout = ((Draggable) component).getLayout();
            tool.addComponentToGridCell(targetLayout, layout, col, row);
        }
    }
}
