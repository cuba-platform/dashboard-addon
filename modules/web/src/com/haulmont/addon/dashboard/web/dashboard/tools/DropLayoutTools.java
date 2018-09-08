/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.tools;

import com.haulmont.addon.dashboard.model.visualmodel.DashboardLayout;
import com.haulmont.addon.dashboard.model.visualmodel.GridLayout;
import com.haulmont.addon.dashboard.model.visualmodel.WidgetLayout;
import com.haulmont.addon.dashboard.web.dashboard.events.DashboardRefreshEvent;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.grid.GridCreationDialog;
import com.haulmont.addon.dashboard.web.dashboard.layouts.*;
import com.haulmont.addon.dashboard.web.dashboard.tools.drophandler.HorizontalLayoutDropHandler;
import com.haulmont.addon.dashboard.web.dashboard.tools.drophandler.NotDropHandler;
import com.haulmont.addon.dashboard.web.dashboard.tools.drophandler.VerticalLayoutDropHandler;
import com.haulmont.addon.dashboard.web.widget.WidgetEdit;
import com.haulmont.addon.dnd.web.gui.components.WebDDGridLayout;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.gui.components.BoxLayout;
import com.haulmont.cuba.gui.components.Component;

import java.util.ArrayList;
import java.util.List;

import static com.haulmont.cuba.gui.WindowManager.OpenType.DIALOG;
import static com.haulmont.cuba.gui.WindowManager.OpenType.THIS_TAB;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class DropLayoutTools {//TODO
    protected DashboardModelConverter modelConverter;
    protected CanvasFrame frame;

    public void init(CanvasFrame targetFrame, DashboardModelConverter modelConverter, CanvasLayout rootContainer) {
        this.frame = targetFrame;
        this.modelConverter = modelConverter;
        initDropHandler(rootContainer);
    }

    public void addDropHandler(CanvasLayout layout) {
        if (layout instanceof CanvasVerticalLayout) {
            layout.setDropHandler(new VerticalLayoutDropHandler(this));
        } else if (layout instanceof CanvasHorizontalLayout) {
            layout.setDropHandler(new HorizontalLayoutDropHandler(this));
        } else if (layout instanceof CanvasGridLayout) {
            layout.setDropHandler(new NotDropHandler());
        } else if (layout instanceof CanvasWidgetLayout) {
            layout.setDropHandler(new NotDropHandler());
        }
    }

    public void addComponent(BoxLayout target, DashboardLayout layout) {
        addComponent(target, layout, emptyList());
    }

    public void addComponent(BoxLayout target, DashboardLayout layout, Integer indexTo) {
        addComponent(target, layout, singletonList(indexTo));
    }

    public void addComponent(WebDDGridLayout target, DashboardLayout layout, Integer column, Integer row) {
        addComponent(target, layout, new ArrayList<Object>() {{
            add(column);
            add(row);
        }});
    }

    protected void initDropHandler(Component component) {
        if (component instanceof CanvasLayout) {
            addDropHandler((CanvasLayout) component);
        }
        if (component instanceof Component.Container) {
            for (Component child : ((Component.Container) component).getOwnComponents()) {
                initDropHandler(child);
            }
        }
    }

    protected void addComponent(Component target, DashboardLayout layout, List<Object> args) {//TODO
        if (layout instanceof GridLayout) {
            GridCreationDialog dialog = (GridCreationDialog) frame.openWindow(GridCreationDialog.SCREEN_NAME, DIALOG);
            dialog.addCloseListener(actionId -> {
                if ("commit".equals(actionId)) {
                    int cols = dialog.getCols();
                    int rows = dialog.getRows();

                    CanvasGridLayout canvasLayout = modelConverter.getFactory().createCanvasGridLayout(cols, rows);

                    for (int i = 0; i < cols; i++) {
                        for (int j = 0; j < rows; j++) {
                            CanvasVerticalLayout verticalLayout = modelConverter.getFactory().createCanvasVerticalLayout();
                            addDropHandler(verticalLayout);
                            canvasLayout.addComponent(verticalLayout, i, j);
                        }
                    }
                    addDropHandler(canvasLayout);
                    addCanvasLayout(canvasLayout, target, args);
                }
            });
        } else if (layout instanceof WidgetLayout) {
            WidgetLayout widgetLayout = (WidgetLayout) layout;
            WidgetEdit editor = (WidgetEdit) frame.openEditor(WidgetEdit.SCREEN_NAME, widgetLayout.getWidget(), THIS_TAB);
            editor.addCloseWithCommitListener(() -> {
                widgetLayout.setWidget(editor.getItem());
                addDashboardLayout(widgetLayout, target, args);
            });
        } else {
            addDashboardLayout(layout, target, args);
        }
    }

    protected void addDashboardLayout(DashboardLayout layout, Component target, List<Object> args) {
        CanvasLayout canvasLayout = modelConverter.modelToContainer(frame, layout);

        if (canvasLayout != null) {
            addDropHandler(canvasLayout);
            addCanvasLayout(canvasLayout, target, args);
        }
    }

    protected void addCanvasLayout(CanvasLayout canvasLayout, Component target, List<Object> args) {
        if (args.size() == 0) {
            ((BoxLayout) target).add(canvasLayout);
        } else if (args.size() == 1) {
            ((BoxLayout) target).add(canvasLayout, (int) args.get(0));
        } else if (args.size() == 2) {
            ((WebDDGridLayout) target).add(canvasLayout, (int) args.get(0), (int) args.get(1));
        }

        canvasLayout.setWeight(1);

        Events events = AppBeans.get(Events.class);
        events.publish(new DashboardRefreshEvent(frame.getDashboardModel(), canvasLayout.getUuid()));
    }
}
