/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.tools;

import com.audimex.dashboard.model.visual_model.DashboardLayout;
import com.audimex.dashboard.model.visual_model.GridLayout;
import com.audimex.dashboard.model.visual_model.HorizontalLayout;
import com.audimex.dashboard.model.visual_model.VerticalLayout;
import com.audimex.dashboard.web.dashboard.drop_handlers.GridLayoutDropHandler;
import com.audimex.dashboard.web.dashboard.drop_handlers.HorizontalLayoutDropHandler;
import com.audimex.dashboard.web.dashboard.drop_handlers.VerticalLayoutDropHandler;
import com.audimex.dashboard.web.dashboard.frames.canvas.CanvasFrame;
import com.audimex.dashboard.web.dashboard.frames.grid_creation_dialog.GridCreationDialog;
import com.audimex.dashboard.web.dashboard.layouts.*;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import fi.jasoft.dragdroplayouts.DDGridLayout;

import java.util.ArrayList;
import java.util.List;

import static com.haulmont.cuba.gui.WindowManager.OpenType.DIALOG;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@org.springframework.stereotype.Component
public class DropLayoutTools {

    protected CanvasFrame targetFrame;

    public void init(CanvasFrame targetFrame, CanvasLayout rootContainer) {
        this.targetFrame = targetFrame;
        addDropHandler(rootContainer);
    }

    public void addComponent(AbstractLayout target, DashboardLayout layout) {
        addComponent(target, layout, emptyList());
    }

    public void addComponent(AbstractLayout target, DashboardLayout layout, Integer indexTo) {
        addComponent(target, layout, singletonList(indexTo));
    }

    public void addComponent(AbstractLayout target, DashboardLayout layout, Integer column, Integer row) {
        addComponent(target, layout, new ArrayList<Object>() {{
            add(column);
            add(row);
        }});
    }

    protected void addComponent(AbstractLayout target, DashboardLayout layout, List<Object> args) {
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
                    addCanvasLayout(canvasLayout, target, args);
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
                addCanvasLayout(canvasLayout, target, args);
            }
        }
    }

    protected void addCanvasLayout(CanvasLayout canvasLayout, AbstractLayout target, List<Object> args) {
        if (args.size() == 0) {
            target.addComponent(canvasLayout);
        } else if (args.size() == 1) {
            ((AbstractOrderedLayout) target).addComponent(canvasLayout, (int) args.get(0));
        } else if (args.size() == 2) {
            ((DDGridLayout) target).addComponent(canvasLayout, (int) args.get(0), (int) args.get(1));
        }
    }

    public void addDropHandler(CanvasLayout layout) {
        if (layout instanceof CanvasVerticalLayout) {
            layout.setDropHandler(new VerticalLayoutDropHandler(this));
        } else if (layout instanceof CanvasHorizontalLayout) {
            layout.setDropHandler(new HorizontalLayoutDropHandler(this));
        } else if (layout instanceof CanvasGridLayout) {
            layout.setDropHandler(new GridLayoutDropHandler(this));
        } else if (layout instanceof CanvasWidgetLayout) {

        }
    }
}
