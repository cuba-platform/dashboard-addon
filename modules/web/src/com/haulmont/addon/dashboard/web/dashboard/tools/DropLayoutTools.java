/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.tools;

import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.model.visualmodel.*;
import com.haulmont.addon.dashboard.web.dashboard.events.DashboardRefreshEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.WidgetTreeEvent;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.grid.GridCreationDialog;
import com.haulmont.addon.dashboard.web.widget.WidgetEdit;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.haulmont.addon.dashboard.utils.DashboardLayoutUtils.findLayout;
import static com.haulmont.addon.dashboard.utils.DashboardLayoutUtils.findParentLayout;
import static com.haulmont.cuba.gui.WindowManager.OpenType.DIALOG;
import static com.haulmont.cuba.gui.WindowManager.OpenType.THIS_TAB;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class DropLayoutTools {
    protected DashboardModelConverter modelConverter;
    protected AbstractFrame frame;
    protected Dashboard dashboard;
    private Metadata metadata = AppBeans.get(Metadata.class);
    private Events events = AppBeans.get(Events.class);

    public DropLayoutTools(AbstractFrame frame, Dashboard dashboard, DashboardModelConverter modelConverter) {
        this.modelConverter = modelConverter;
        this.frame = frame;
        this.dashboard = dashboard;
    }

    public void init(AbstractFrame targetFrame, DashboardModelConverter modelConverter, Dashboard dashboard) {
        this.frame = targetFrame;
        this.modelConverter = modelConverter;
        this.dashboard = dashboard;
    }

    public void addComponent(UUID target, DashboardLayout layout) {
        addComponent(target, layout, emptyList());
    }

    public void addComponent(UUID target, DashboardLayout layout, Integer indexTo) {
        addComponent(target, layout, singletonList(indexTo));
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

    public RootLayout moveComponent(Dashboard dashboard, UUID sourceLayoutId, UUID targetLayoutId, WidgetTreeEvent.DropLocation location) {
        RootLayout dashboardModel = dashboard.getVisualModel();
        DashboardLayout target = findLayout(dashboardModel, targetLayoutId);
        DashboardLayout layout = findLayout(dashboardModel, sourceLayoutId);
        DashboardLayout parent = findParentLayout(dashboardModel, sourceLayoutId);

        parent.removeOwnChild(layout);

        if (target instanceof ContainerLayout) {
            switch (location) {
                case MIDDLE:
                    target.addChild(layout);
                    break;
                case BOTTOM:
                    List<DashboardLayout> newChildren = new ArrayList<>();
                    newChildren.add(layout);
                    newChildren.addAll(parent.getChildren());
                    parent.setChildren(newChildren);
                    break;
                case TOP:
                    newChildren = new ArrayList<>(parent.getChildren());
                    newChildren.add(layout);
                    parent.setChildren(newChildren);
                    break;
            }
        }
        if (target instanceof WidgetLayout) {
            List<DashboardLayout> newChildren = new ArrayList<>();
            for (DashboardLayout childLayout : parent.getChildren()) {
                if (childLayout.getId().equals(target.getId())) {
                    switch (location) {
                        case TOP:
                            newChildren.add(layout);
                            newChildren.add(childLayout);
                            break;
                        case MIDDLE:
                        case BOTTOM:
                            newChildren.add(childLayout);
                            newChildren.add(layout);
                            break;
                    }
                } else {
                    newChildren.add(childLayout);
                }
            }
            parent.setChildren(newChildren);
        }
        return dashboardModel;
    }

    public AbstractFrame getFrame() {
        return frame;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }
}
