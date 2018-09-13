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

    public void addComponent(DashboardLayout layout, UUID targetLayoutUuid, WidgetTreeEvent.DropLocation location) {
        DashboardLayout targetLayout = findLayout(dashboard.getVisualModel(), targetLayoutUuid);
        if (layout instanceof GridLayout) {
            GridCreationDialog dialog = (GridCreationDialog) frame.openWindow(GridCreationDialog.SCREEN_NAME, DIALOG);
            dialog.addCloseListener(actionId -> {
                if (Window.COMMIT_ACTION_ID.equals(actionId)) {
                    int cols = dialog.getCols();
                    int rows = dialog.getRows();

                    GridLayout gridLayout = metadata.create(GridLayout.class);
                    gridLayout.setColumns(cols);
                    gridLayout.setRows(rows);

                    for (int i = 0; i < cols; i++) {
                        for (int j = 0; j < rows; j++) {
                            GridArea gridArea = metadata.create(GridArea.class);
                            gridArea.setCol1(i);
                            gridArea.setRow1(j);
                            gridArea.setComponent(metadata.create(VerticalLayout.class));
                            gridLayout.addArea(gridArea);
                        }
                    }
                    reorderWidgetsAndPushEvents(gridLayout, targetLayout, location);
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
                reorderWidgetsAndPushEvents(widgetLayout, targetLayout, location);
            });
        } else if (layout instanceof VerticalLayout) {
            reorderWidgetsAndPushEvents(metadata.create(VerticalLayout.class), targetLayout, location);
        } else if (layout instanceof HorizontalLayout) {
            reorderWidgetsAndPushEvents(metadata.create(HorizontalLayout.class), targetLayout, location);
        }
    }

    private void reorderWidgetsAndPushEvents(DashboardLayout layout, DashboardLayout targetLayout, WidgetTreeEvent.DropLocation location) {
        DashboardLayout parentLayout = targetLayout instanceof WidgetLayout ?
                findParentLayout(dashboard.getVisualModel(), targetLayout) : targetLayout;
        parentLayout.addChild(layout);
        moveComponent(layout, targetLayout.getId(), location);
        events.publish(new DashboardRefreshEvent(dashboard.getVisualModel()));
    }

    public void moveComponent(DashboardLayout layout, UUID targetLayoutId, WidgetTreeEvent.DropLocation location) {
        RootLayout dashboardModel = dashboard.getVisualModel();
        DashboardLayout target = findLayout(dashboardModel, targetLayoutId);
        DashboardLayout parent = findParentLayout(dashboardModel, layout);

        if (target.getId().equals(layout.getId())) {
            return;
        }

        parent.removeOwnChild(layout);

        if (target instanceof ContainerLayout) {
            switch (location) {
                case MIDDLE:
                    target.addChild(layout);
                    break;
                case BOTTOM:
                case RIGHT:
                    List<DashboardLayout> newChildren = new ArrayList<>();
                    newChildren.add(layout);
                    newChildren.addAll(parent.getChildren());
                    parent.setChildren(newChildren);
                    break;
                case TOP:
                case LEFT:
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
                        case LEFT:
                            newChildren.add(layout);
                            newChildren.add(childLayout);
                            break;
                        case MIDDLE:
                        case BOTTOM:
                        case RIGHT:
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
    }

    public AbstractFrame getFrame() {
        return frame;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }
}
