/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.tools;

import com.audimex.dashboard.model.visual_model.*;
import com.audimex.dashboard.web.dashboard.drop_handlers.GridLayoutDropHandler;
import com.audimex.dashboard.web.dashboard.drop_handlers.HorizontalLayoutDropHandler;
import com.audimex.dashboard.web.dashboard.drop_handlers.VerticalLayoutDropHandler;
import com.audimex.dashboard.web.dashboard.frames.canvas.CanvasFrame;
import com.audimex.dashboard.web.dashboard.frames.grid_dialog.GridDialog;
import com.haulmont.addon.dnd.components.DDGridLayout;
import com.haulmont.addon.dnd.components.DDHorizontalLayout;
import com.haulmont.addon.dnd.components.DDVerticalLayout;
import com.haulmont.cuba.gui.components.BoxLayout;
import com.haulmont.cuba.gui.components.Component.Container;

import javax.inject.Inject;

import static com.haulmont.cuba.gui.WindowManager.OpenType.DIALOG;

@org.springframework.stereotype.Component
public class DropLayoutTool {
    @Inject
    protected LayoutFactory factory;

    protected CanvasFrame frame;

    public CanvasFrame getFrame() {
        return frame;
    }

    public void setFrame(CanvasFrame frame) {
        this.frame = frame;
    }

    public void addComponent(BoxLayout targetLayout, DashboardLayout layout) {
        addComponent(targetLayout, layout, targetLayout.getOwnComponents().size());
    }

    public void addComponent(BoxLayout target, DashboardLayout layout, int indexTo) {
        if (layout instanceof VerticalLayout) {
            DDVerticalLayout verticalLayout = factory.createVerticalLayout();
            verticalLayout.setDropHandler(new VerticalLayoutDropHandler(this));
            target.add(verticalLayout, indexTo);
        } else if (layout instanceof HorizontalLayout) {
            DDHorizontalLayout horizontalLayout = factory.createHorizontalLayout();
            horizontalLayout.setDropHandler(new HorizontalLayoutDropHandler(this));
            target.add(horizontalLayout, indexTo);
        } else if (layout instanceof GridLayout) {
            GridDialog dialog = (GridDialog) frame.openWindow(GridDialog.SCREEN_NAME, DIALOG);
            dialog.addCloseListener(actionId -> {
                if (GridDialog.APPLY.equals(actionId)) {
                    DDGridLayout gridLayout = factory.createGridLayout(dialog.getCols(), dialog.getRows());
                    gridLayout.setDropHandler(new GridLayoutDropHandler(this));
                    gridLayout.getOwnComponents().forEach(vLayout ->
                            ((DDVerticalLayout) vLayout).setDropHandler(new VerticalLayoutDropHandler(this))
                    );

                    target.add(gridLayout, indexTo);
                }
            });
        } else if (layout instanceof WidgetLayout) {
            Container widgetLayout = factory.createWidgetLayout(((WidgetLayout) layout).getWidget(), frame);
            target.add(widgetLayout, indexTo);
        }
    }

    public void addComponentToGridCell(DDGridLayout target, DashboardLayout layout, int row, int col) {
//        if (layout instanceof VerticalLayout) {
//            DDVerticalLayout verticalLayout = factory.createVerticalLayout(this);
//            target.add(verticalLayout, row, col);
//        } else if (layout instanceof HorizontalLayout) {
//            DDHorizontalLayout horizontalLayout = factory.createHorizontalLayout(this);
//            target.add(horizontalLayout, row, col);
//        } else if (layout instanceof GridLayout) {
//            GridDialog dialog = (GridDialog) frame.openWindow(GridDialog.SCREEN_NAME, DIALOG);
//            dialog.addCloseListener(actionId -> {
//                if (GridDialog.APPLY.equals(actionId)) {
//                    DDGridLayout gridLayout = factory.createGridLayout(dialog.getCols(), dialog.getRows(), this);
//                    target.add(gridLayout, row, col);
//                }
//            });
//        } else if (layout instanceof WidgetLayout) {
//            Container widgetLayout = factory.createWidgetLayout(((WidgetLayout) layout).getWidget(), frame);
//            target.add(widgetLayout, row, col);
//        }
    }
}
