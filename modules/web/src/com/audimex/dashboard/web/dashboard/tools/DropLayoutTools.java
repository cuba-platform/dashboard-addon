/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.tools;

import com.audimex.dashboard.model.visual_model.*;
import com.audimex.dashboard.web.dashboard.drop_handlers.HorizontalLayoutDropHandler;
import com.audimex.dashboard.web.dashboard.drop_handlers.NotDropHandler;
import com.audimex.dashboard.web.dashboard.drop_handlers.VerticalLayoutDropHandler;
import com.audimex.dashboard.web.dashboard.frames.canvas.CanvasFrame;
import com.audimex.dashboard.web.dashboard.frames.grid_creation_dialog.GridCreationDialog;
import com.audimex.dashboard.web.dashboard.layouts.*;
import com.vaadin.ui.AbstractOrderedLayout;

import static com.haulmont.cuba.gui.WindowManager.OpenType.DIALOG;

@org.springframework.stereotype.Component
public class DropLayoutTools {

    protected CanvasFrame targetFrame;

    public void init(CanvasFrame targetFrame, CanvasLayout rootContainer) {
        this.targetFrame = targetFrame;
        addDropHandler(rootContainer);
    }

    public void addComponent(AbstractOrderedLayout targetLayout, DashboardLayout layout) {
        addComponent(targetLayout, layout, null);
    }

    public void addComponent(AbstractOrderedLayout target, DashboardLayout layout, Integer indexTo) {
        if (layout instanceof GridLayout) {
            GridCreationDialog dialog = (GridCreationDialog) targetFrame.openWindow(GridCreationDialog.SCREEN_NAME, DIALOG);
            dialog.addCloseListener(actionId -> {
                if (GridCreationDialog.APPLY.equals(actionId)) {
                    int cols = dialog.getCols();
                    int rows = dialog.getRows();

                    CanvasGridLayout canvasLayout = new CanvasGridLayout(cols, rows);

                    for (int i = 0; i < cols; i++) {
                        for (int j = 0; j < rows; j++) {
                            CanvasVerticalLayout verticalLayout = new CanvasVerticalLayout();
                            addDropHandler(verticalLayout);
                            canvasLayout.addComponent(verticalLayout, i, j);
                        }
                    }

                    addDropHandler(canvasLayout);
                    addCanvasLayout(canvasLayout, target, indexTo);
                }
            });
        } else {
            CanvasLayout canvasLayout = null;

            if (layout instanceof VerticalLayout) {
                canvasLayout = new CanvasVerticalLayout();
            } else if (layout instanceof HorizontalLayout) {
                canvasLayout = new CanvasHorizontalLayout();
            }

            if (canvasLayout != null) {
                addDropHandler(canvasLayout);
                addCanvasLayout(canvasLayout, target, indexTo);
            }
        }

    }

    protected void addCanvasLayout(CanvasLayout canvasLayout, AbstractOrderedLayout target, Integer indexTo) {
        if (indexTo == null) {
            target.addComponent(canvasLayout);
        } else {
            target.addComponent(canvasLayout, indexTo);
        }
    }

    public void addDropHandler(CanvasLayout layout) {
        if (layout instanceof CanvasVerticalLayout) {
            layout.setDropHandler(new VerticalLayoutDropHandler(this));
        } else if (layout instanceof CanvasHorizontalLayout) {
            layout.setDropHandler(new HorizontalLayoutDropHandler(this));
        } else if (layout instanceof CanvasGridLayout) {
            layout.setDropHandler(new NotDropHandler());
        } else if (layout instanceof CanvasWidgetLayout) {

        }
    }
}
