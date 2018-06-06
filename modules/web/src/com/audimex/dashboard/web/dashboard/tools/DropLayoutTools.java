/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.tools;

import com.audimex.dashboard.model.visual_model.DashboardLayout;
import com.audimex.dashboard.model.visual_model.GridLayout;
import com.audimex.dashboard.model.visual_model.WidgetLayout;
import com.audimex.dashboard.web.dashboard.frames.canvas.CanvasFrame;
import com.audimex.dashboard.web.dashboard.frames.editor.grid_creation_dialog.GridCreationDialog;
import com.audimex.dashboard.web.dashboard.layouts.*;
import com.audimex.dashboard.web.dashboard.tools.drop_handlers.GridLayoutDropHandler;
import com.audimex.dashboard.web.dashboard.tools.drop_handlers.HorizontalLayoutDropHandler;
import com.audimex.dashboard.web.dashboard.tools.drop_handlers.NotDropHandler;
import com.audimex.dashboard.web.dashboard.tools.drop_handlers.VerticalLayoutDropHandler;
import com.audimex.dashboard.web.widget.WidgetEdit;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import fi.jasoft.dragdroplayouts.DDGridLayout;

import java.util.ArrayList;
import java.util.List;

import static com.haulmont.cuba.gui.WindowManager.OpenType.DIALOG;
import static com.haulmont.cuba.gui.WindowManager.OpenType.THIS_TAB;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class DropLayoutTools {
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
            layout.setDropHandler(new GridLayoutDropHandler(this));
        } else if (layout instanceof CanvasWidgetLayout) {
            layout.setDropHandler(new NotDropHandler());
        }
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

    protected void initDropHandler(Component component) {
        if (component instanceof CanvasLayout) {
            addDropHandler((CanvasLayout) component);
        }
        if (component instanceof HasComponents) {
            for (Component child : ((HasComponents) component)) {
                initDropHandler(child);
            }
        }
    }

    protected void addComponent(AbstractLayout target, DashboardLayout layout, List<Object> args) {
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

    protected void addDashboardLayout(DashboardLayout layout, AbstractLayout target, List<Object> args) {
        CanvasLayout canvasLayout = modelConverter.modelToContainer(frame, layout);

        if (canvasLayout != null) {
            addDropHandler(canvasLayout);
            addCanvasLayout(canvasLayout, target, args);
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
}
