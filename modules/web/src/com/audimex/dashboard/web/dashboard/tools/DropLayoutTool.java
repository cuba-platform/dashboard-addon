/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.tools;

import com.audimex.dashboard.model.visual_model.*;
import com.audimex.dashboard.web.dashboard.drop_handlers.GridLayoutDropHandler;
import com.audimex.dashboard.web.dashboard.drop_handlers.HorizontalLayoutDropHandler;
import com.audimex.dashboard.web.dashboard.drop_handlers.NotDropHandler;
import com.audimex.dashboard.web.dashboard.drop_handlers.VerticalLayoutDropHandler;
import com.audimex.dashboard.web.dashboard.frames.canvas.CanvasFrame;
import com.audimex.dashboard.web.dashboard.frames.grid_dialog.GridDialog;
import com.audimex.dashboard.web.widget_types.AbstractWidgetBrowse;
import com.haulmont.addon.dnd.components.DDGridLayout;
import com.haulmont.addon.dnd.components.DDHorizontalLayout;
import com.haulmont.addon.dnd.components.DDLayout;
import com.haulmont.addon.dnd.components.DDVerticalLayout;
import com.haulmont.cuba.gui.components.BoxLayout;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Component.Container;
import com.haulmont.cuba.gui.components.Frame;

import javax.inject.Inject;

import java.util.Collection;

import static com.haulmont.cuba.gui.WindowManager.OpenType.DIALOG;

@org.springframework.stereotype.Component
public class DropLayoutTool {
    @Inject
    protected LayoutFactory factory;

    protected Frame targetFrame;

    public void init(Frame targetFrame, DDLayout rootContainer) {
        this.targetFrame = targetFrame;
        addDropHandler(rootContainer);
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
            GridDialog dialog = (GridDialog) targetFrame.openWindow(GridDialog.SCREEN_NAME, DIALOG);
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
            Container widgetLayout = factory.createWidgetLayout(((WidgetLayout) layout).getWidget(), targetFrame);
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
//            GridDialog dialog = (GridDialog) targetFrame.openWindow(GridDialog.SCREEN_NAME, DIALOG);
//            dialog.addCloseListener(actionId -> {
//                if (GridDialog.APPLY.equals(actionId)) {
//                    DDGridLayout gridLayout = factory.createGridLayout(dialog.getCols(), dialog.getRows(), this);
//                    target.add(gridLayout, row, col);
//                }
//            });
//        } else if (layout instanceof WidgetLayout) {
//            Container widgetLayout = factory.createWidgetLayout(((WidgetLayout) layout).getWidget(), targetFrame);
//            target.add(widgetLayout, row, col);
//        }
    }

    public void addDropHandler(DDLayout container) {
        if (container instanceof DDVerticalLayout) {
            container.setDropHandler(new VerticalLayoutDropHandler(this));
            addDropHandlers(((DDVerticalLayout) container).getOwnComponents());
        } else if (container instanceof DDHorizontalLayout) {
            container.setDropHandler(new HorizontalLayoutDropHandler(this));
            addDropHandlers(((DDHorizontalLayout) container).getOwnComponents());
        } else if (container instanceof AbstractWidgetBrowse) {
            container.setDropHandler(new NotDropHandler());
        } else if (container instanceof DDGridLayout) {
            container.setDropHandler(new GridLayoutDropHandler(this));
            addDropHandlers(((DDGridLayout) container).getOwnComponents());
        }
    }

    protected void addDropHandlers(Collection<Component> containers) {
        for (Component container : containers) {
            addDropHandler((DDLayout) container);
        }
    }
}
