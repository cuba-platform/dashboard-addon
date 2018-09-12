/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.tools;

import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.model.visualmodel.*;
import com.haulmont.addon.dashboard.web.dashboard.events.DashboardRefreshEvent;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.components.Draggable;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.grid.GridCreationDialog;
import com.haulmont.addon.dashboard.web.dashboard.layouts.*;
import com.haulmont.addon.dashboard.web.dashboard.tools.drophandler.HorizontalLayoutDropHandler;
import com.haulmont.addon.dashboard.web.dashboard.tools.drophandler.LayoutDropHandler;
import com.haulmont.addon.dashboard.web.dashboard.tools.drophandler.NotDropHandler;
import com.haulmont.addon.dashboard.web.dashboard.tools.drophandler.VerticalLayoutDropHandler;
import com.haulmont.addon.dashboard.web.widget.WidgetEdit;
import com.haulmont.addon.dnd.web.gui.components.WebDDGridLayout;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.BoxLayout;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Window;
import com.vaadin.event.DataBoundTransferable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.haulmont.addon.dashboard.web.dashboard.datasources.DashboardLayoutUtils.findLayout;
import static com.haulmont.cuba.gui.WindowManager.OpenType.DIALOG;
import static com.haulmont.cuba.gui.WindowManager.OpenType.THIS_TAB;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class DropLayoutTools {
    protected DashboardModelConverter modelConverter;
    protected CanvasFrame frame;
    protected Dashboard dashboard;
    private Metadata metadata = AppBeans.get(Metadata.class);
    private Events events = AppBeans.get(Events.class);

    public void init(CanvasFrame targetFrame, DashboardModelConverter modelConverter, Dashboard dashboard) {
        this.frame = targetFrame;
        this.modelConverter = modelConverter;
        this.dashboard = dashboard;
    }

    public void addDropHandler(CanvasLayout layout) {
        if (layout instanceof CanvasVerticalLayout) {
            layout.setDropHandler(new LayoutDropHandler(this));
        } else if (layout instanceof CanvasHorizontalLayout) {
            layout.setDropHandler(new LayoutDropHandler(this));
        } else if (layout instanceof CanvasGridLayout) {
            layout.setDropHandler(new NotDropHandler());
        } else if (layout instanceof CanvasWidgetLayout) {
            layout.setDropHandler(new NotDropHandler());
        }
    }

    public void addComponent(UUID target, DashboardLayout layout) {
        addComponent(target, layout, emptyList());
    }

    public void addComponent(UUID target, DashboardLayout layout, Integer indexTo) {
        addComponent(target, layout, singletonList(indexTo));
    }

    public void initDropHandler(Component component) {
        if (component instanceof CanvasLayout) {
            addDropHandler((CanvasLayout) component);
        }
        if (component instanceof Component.Container) {
            for (Component child : ((Component.Container) component).getOwnComponents()) {
                initDropHandler(child);
            }
        }
    }

    protected void addComponent(UUID targetLayoutUuid, DashboardLayout layout, List<Object> args) {
        if (layout instanceof GridLayout) {
            GridCreationDialog dialog = (GridCreationDialog) frame.openWindow(GridCreationDialog.SCREEN_NAME, DIALOG);
            dialog.addCloseListener(actionId -> {
                if (Window.COMMIT_ACTION_ID.equals(actionId)) {
                    int cols = dialog.getCols();
                    int rows = dialog.getRows();

                    //CanvasGridLayout canvasLayout = modelConverter.getFactory().createCanvasGridLayout(cols, rows);
                    GridLayout gridLayout = metadata.create(GridLayout.class);
                    gridLayout.setColumns(cols);
                    gridLayout.setRows(rows);

                    for (int i = 0; i < cols; i++) {
                        for (int j = 0; j < rows; j++) {
                            //CanvasVerticalLayout verticalLayout = modelConverter.getFactory().createCanvasVerticalLayout();
                            //addDropHandler(verticalLayout);
                            GridArea gridArea = metadata.create(GridArea.class);
                            gridArea.setCol1(i);
                            gridArea.setRow1(j);
                            gridArea.setComponent(metadata.create(VerticalLayout.class));
                            //canvasLayout.addComponent(verticalLayout, i, j);
                            gridLayout.addArea(gridArea);
                        }
                    }
                    //addDropHandler(canvasLayout);
                    //addCanvasLayout(canvasLayout, target, args);
                    DashboardLayout parentDashboardLayout = findLayout(dashboard.getVisualModel(), targetLayoutUuid);
                    parentDashboardLayout.addChild(gridLayout);
                    events.publish(new DashboardRefreshEvent(dashboard.getVisualModel()));
                }
            });
        } else if (layout instanceof WidgetLayout) {
            WidgetLayout widgetLayout = metadata.create(WidgetLayout.class);
            Widget widget = metadata.create(((WidgetLayout) layout).getWidget().getClass());
            widget.setBrowseFrameId(((WidgetLayout) layout).getWidget().getBrowseFrameId());
            widget.setDashboard(((WidgetLayout) layout).getWidget().getDashboard());
            WidgetEdit editor = (WidgetEdit) frame.openEditor(WidgetEdit.SCREEN_NAME, widget, THIS_TAB);
            editor.addCloseWithCommitListener(() -> {
                widgetLayout.setWidget(editor.getItem());
                //addDashboardLayout(widgetLayout, target, args);
                DashboardLayout parentDashboardLayout = findLayout(dashboard.getVisualModel(), targetLayoutUuid);
                parentDashboardLayout.addChild(widgetLayout);
                events.publish(new DashboardRefreshEvent(dashboard.getVisualModel()));
            });
        } else if (layout instanceof VerticalLayout) {
            //addDashboardLayout(layout, target, args);
            VerticalLayout verticalLayout = metadata.create(VerticalLayout.class);
            DashboardLayout parentDashboardLayout = findLayout(dashboard.getVisualModel(), targetLayoutUuid);
            parentDashboardLayout.addChild(verticalLayout);
            events.publish(new DashboardRefreshEvent(dashboard.getVisualModel()));
        } else if (layout instanceof HorizontalLayout) {
            //addDashboardLayout(layout, target, args);
            HorizontalLayout horizontalLayout = metadata.create(HorizontalLayout.class);
            DashboardLayout parentDashboardLayout = findLayout(dashboard.getVisualModel(), targetLayoutUuid);
            parentDashboardLayout.addChild(horizontalLayout);
            events.publish(new DashboardRefreshEvent(dashboard.getVisualModel()));
        }
    }

    protected void addDashboardLayout(DashboardLayout layout, Component target, List<Object> args) {
        CanvasLayout canvasLayout = modelConverter.modelToContainer(frame, layout);

        if (canvasLayout != null) {
            addDropHandler(canvasLayout);
            addCanvasLayout(canvasLayout, target, args);
        }
    }

    public void addCanvasLayout(CanvasLayout canvasLayout, Component target, List<Object> args) {
        if (args.size() == 0) {
            ((BoxLayout) target).add(canvasLayout);
        } else if (args.size() == 1) {
            ((BoxLayout) target).add(canvasLayout, (int) args.get(0));
        } else if (args.size() == 2) {
            ((WebDDGridLayout) target).add(canvasLayout, (int) args.get(0), (int) args.get(1));
        }

        canvasLayout.setWeight(1);

//        events.publish(new DashboardRefreshEvent(frame.getDashboardModel(), canvasLayout.getUuid()));
    }

    /*public void addCanvasComponent(BoxLayout target, Component comp, int idx) {
        if (comp instanceof Draggable) {
            if (idx >= 0) {
                addComponent(target, ((Draggable) comp).getLayout(), idx);
            } else {
                addComponent(target, ((Draggable) comp).getLayout());
            }
        }
        if (comp instanceof CanvasLayout && comp.getParent() instanceof Component.OrderedContainer) {
            Component.OrderedContainer parent = (Component.OrderedContainer) comp.getParent();
            if (parent != null) {
                parent.remove(comp);
                if (idx >= 0) {
                    addCanvasLayout((CanvasLayout) comp, target, singletonList(idx));
                } else {
                    addCanvasLayout((CanvasLayout) comp, target, new ArrayList<>());
                }
            }
        }
        if (comp instanceof CanvasLayout && comp.getParent() == null) {
            if (idx >= 0) {
                addCanvasLayout((CanvasLayout) comp, target, singletonList(idx));
            } else {
                addCanvasLayout((CanvasLayout) comp, target, new ArrayList<>());
            }
        }
    }*/

    public CanvasFrame getFrame() {
        return frame;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }
}
