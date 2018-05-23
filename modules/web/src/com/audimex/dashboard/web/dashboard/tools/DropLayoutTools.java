/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.tools;

import com.audimex.dashboard.model.visual_model.*;
import com.audimex.dashboard.web.dashboard.drop_handlers.VerticalLayoutDropHandler;
import com.audimex.dashboard.web.dashboard.frames.canvas.CanvasFrame;
import com.audimex.dashboard.web.dashboard.layouts.DdDashboardHorizontalLayout;
import com.audimex.dashboard.web.dashboard.layouts.DdDashboardLayout;
import com.audimex.dashboard.web.dashboard.layouts.DdDashboardVerticalLayout;
import com.audimex.dashboard.web.dashboard.layouts.DdDashboardWidgetLayout;
import com.haulmont.cuba.gui.components.BoxLayout;

@org.springframework.stereotype.Component
public class DropLayoutTools {

    protected CanvasFrame targetFrame;

    public void init(CanvasFrame targetFrame, DdDashboardLayout rootContainer) {
        this.targetFrame = targetFrame;
        addDropHandler(rootContainer);
    }

    public void addComponent(BoxLayout targetLayout, DashboardLayout layout) {
        addComponent(targetLayout, layout, targetLayout.getOwnComponents().size());
    }

    public void addComponent(BoxLayout target, DashboardLayout layout, int indexTo) {
//        if (layout instanceof VerticalLayout) {
//            DDVerticalLayout verticalLayout = factory.createVerticalLayout();
//            verticalLayout.setDropHandler(new VerticalLayoutDropHandler(this));
//            target.add(verticalLayout, indexTo);
//        } else if (layout instanceof HorizontalLayout) {
//            DDHorizontalLayout horizontalLayout = factory.createHorizontalLayout();
//            horizontalLayout.setDropHandler(new HorizontalLayoutDropHandler(this));
//            target.add(horizontalLayout, indexTo);
//        } else if (layout instanceof GridLayout) {
//            GridDialog dialog = (GridDialog) targetFrame.openWindow(GridDialog.SCREEN_NAME, DIALOG);
//            dialog.addCloseListener(actionId -> {
//                if (GridDialog.APPLY.equals(actionId)) {
//                    DDGridLayout gridLayout = factory.createGridLayout(dialog.getCols(), dialog.getRows(), true);
//                    gridLayout.setDropHandler(new GridLayoutDropHandler(this));
//                    gridLayout.getOwnComponents().forEach(vLayout ->
//                            ((DDVerticalLayout) vLayout).setDropHandler(new VerticalLayoutDropHandler(this))
//                    );
//
//                    target.add(gridLayout, indexTo);
//                }
//            });
//        } else if (layout instanceof WidgetLayout) {
//            Container widgetLayout = factory.createWidgetLayout(((WidgetLayout) layout).getWidget(), targetFrame);
//            target.add(widgetLayout, indexTo);
//        }
    }

    public void addDropHandler(DdDashboardLayout layout) {
        if (layout instanceof DdDashboardVerticalLayout) {
            layout.setDropHandler(new VerticalLayoutDropHandler(this));
//            addDropHandlers(((DDVerticalLayout) layout).getOwnComponents());
        } else if (layout instanceof DdDashboardHorizontalLayout) {
//            layout.setDropHandler(new HorizontalLayoutDropHandler(this));
//            addDropHandlers(((DDHorizontalLayout) layout).getOwnComponents());
        } else if (layout instanceof DdDashboardWidgetLayout) {
//            layout.setDropHandler(new NotDropHandler());
//        } else if (layout instanceof DdDashboardGridLayout) {
//            layout.setDropHandler(new GridLayoutDropHandler(this));
//            addDropHandlers(((DDGridLayout) layout).getOwnComponents());
        }
    }
}
