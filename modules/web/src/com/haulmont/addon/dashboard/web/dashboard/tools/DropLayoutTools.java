/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.addon.dashboard.web.dashboard.tools;

import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.model.visualmodel.*;
import com.haulmont.addon.dashboard.web.dashboard.events.DashboardRefreshEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.WidgetDropLocation;
import com.haulmont.addon.dashboard.web.dashboard.events.WidgetSelectedEvent;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.DashboardEdit;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.css.CssLayoutCreationDialog;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.grid.GridCreationDialog;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.responsive.ResponsiveCreationDialog;
import com.haulmont.addon.dashboard.web.widget.WidgetEdit;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.MetadataTools;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.AbstractDatasource;
import com.haulmont.cuba.gui.screen.OpenMode;
import com.haulmont.cuba.gui.screen.StandardCloseAction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.haulmont.addon.dashboard.utils.DashboardLayoutUtils.*;

public class DropLayoutTools {
    protected DashboardEdit frame;
    protected Datasource<Dashboard> dashboardDs;
    private Metadata metadata = AppBeans.get(Metadata.class);
    private Events events = AppBeans.get(Events.class);
    private MetadataTools metadataTools = AppBeans.get(MetadataTools.class);
    private ScreenBuilders screenBuilders = AppBeans.get(ScreenBuilders.class);

    public DropLayoutTools(DashboardEdit frame, Datasource<Dashboard> dashboardDs) {
        this.frame = frame;
        this.dashboardDs = dashboardDs;
    }

    public Dashboard getDashboard() {
        return frame.getDashboard();
    }

    public void addComponent(DashboardLayout layout, UUID targetLayoutUuid, WidgetDropLocation location) {
        DashboardLayout targetLayout = findLayout(getDashboard().getVisualModel(), targetLayoutUuid);
        if (layout instanceof CssLayout) {
            screenBuilders.screen(frame)
                    .withScreenClass(CssLayoutCreationDialog.class)
                    .withLaunchMode(OpenMode.DIALOG)
                    .build()
                    .show()
                    .addAfterCloseListener(e -> {
                        StandardCloseAction closeAction = (StandardCloseAction) e.getCloseAction();
                        if (Window.COMMIT_ACTION_ID.equals(closeAction.getActionId())) {
                            CssLayoutCreationDialog dialog = (CssLayoutCreationDialog) e.getScreen();
                            CssLayout cssLayout = metadata.create(CssLayout.class);
                            cssLayout.setResponsive(dialog.getResponsive());
                            cssLayout.setStyleName(dialog.getCssStyleName());
                            reorderWidgetsAndPushEvents(cssLayout, targetLayout, location);
                        }
                    });
        }
        if (layout instanceof GridLayout) {
            screenBuilders.screen(frame)
                    .withScreenClass(GridCreationDialog.class)
                    .withLaunchMode(OpenMode.DIALOG)
                    .build()
                    .show()
                    .addAfterCloseListener(e -> {
                        StandardCloseAction closeAction = (StandardCloseAction) e.getCloseAction();
                        if (Window.COMMIT_ACTION_ID.equals(closeAction.getActionId())) {
                            GridCreationDialog dialog = (GridCreationDialog) e.getScreen();
                            int cols = dialog.getCols();
                            int rows = dialog.getRows();

                            GridLayout gridLayout = metadata.create(GridLayout.class);
                            gridLayout.setColumns(cols);
                            gridLayout.setRows(rows);

                            for (int i = 0; i < cols; i++) {
                                for (int j = 0; j < rows; j++) {
                                    GridArea gridArea = metadata.create(GridArea.class);
                                    gridArea.setRow(j);
                                    gridArea.setCol(i);
                                    gridArea.setRow2(j);
                                    gridArea.setCol2(i);
                                    GridCellLayout gcl = metadata.create(GridCellLayout.class);
                                    gcl.setRow(j);
                                    gcl.setColumn(i);
                                    gridArea.setComponent(gcl);
                                    gcl.setParent(gridLayout);
                                    gridLayout.addArea(gridArea);
                                }
                            }
                            reorderWidgetsAndPushEvents(gridLayout, targetLayout, location);
                        }
                    });
        } else if (layout instanceof WidgetTemplateLayout) {
            WidgetLayout widgetLayout = metadata.create(WidgetLayout.class);
            Widget widget = metadataTools.copy(((WidgetTemplateLayout) layout).getWidget());
            widget.setId(UUID.randomUUID());
            widget.setDashboard(((WidgetLayout) layout).getWidget().getDashboard());
            widgetLayout.setWidget(widget);
            reorderWidgetsAndPushEvents(widgetLayout, targetLayout, location);
        } else if (layout instanceof WidgetLayout) {
            WidgetLayout widgetLayout = metadata.create(WidgetLayout.class);
            Widget widget = metadata.create(((WidgetLayout) layout).getWidget().getClass());
            widget.setFrameId(((WidgetLayout) layout).getWidget().getFrameId());
            widget.setName(((WidgetLayout) layout).getWidget().getName());
            widget.setDashboard(((WidgetLayout) layout).getWidget().getDashboard());
            screenBuilders.editor(Widget.class, frame)
                    .newEntity(widget)
                    .withLaunchMode(OpenMode.DIALOG)
                    .build()
                    .show()
                    .addAfterCloseListener(e -> {
                        StandardCloseAction closeAction = (StandardCloseAction) e.getCloseAction();
                        if (Window.COMMIT_ACTION_ID.equals(closeAction.getActionId())) {
                            widgetLayout.setWidget(((WidgetEdit) e.getScreen()).getItem());
                            ((AbstractDatasource) dashboardDs).setModified(true);
                            reorderWidgetsAndPushEvents(widgetLayout, targetLayout, location);
                        }
                    });
        } else if (layout instanceof VerticalLayout) {
            reorderWidgetsAndPushEvents(metadata.create(VerticalLayout.class), targetLayout, location);
        } else if (layout instanceof HorizontalLayout) {
            reorderWidgetsAndPushEvents(metadata.create(HorizontalLayout.class), targetLayout, location);
        } else if (layout instanceof ResponsiveLayout) {
            screenBuilders.screen(frame)
                    .withScreenClass(ResponsiveCreationDialog.class)
                    .withLaunchMode(OpenMode.DIALOG)
                    .build()
                    .show()
                    .addAfterCloseListener(e -> {
                        StandardCloseAction closeAction = (StandardCloseAction) e.getCloseAction();
                        ResponsiveCreationDialog dialog = (ResponsiveCreationDialog) e.getSource();
                        if (Window.COMMIT_ACTION_ID.equals(closeAction.getActionId())) {
                            int xs = dialog.getXs();
                            int sm = dialog.getSm();
                            int md = dialog.getMd();
                            int lg = dialog.getLg();

                            ResponsiveLayout responsiveLayout = metadata.create(ResponsiveLayout.class);
                            responsiveLayout.setXs(xs);
                            responsiveLayout.setSm(sm);
                            responsiveLayout.setMd(md);
                            responsiveLayout.setLg(lg);

                            reorderWidgetsAndPushEvents(responsiveLayout, targetLayout, location);
                        }
                    });
        }

    }

    private void reorderWidgetsAndPushEvents(DashboardLayout layout, DashboardLayout targetLayout, WidgetDropLocation location) {
        DashboardLayout parentLayout = targetLayout instanceof WidgetLayout ?
                findParentLayout(getDashboard().getVisualModel(), targetLayout) : targetLayout;
        parentLayout.addChild(layout);
        layout.setParent(parentLayout);
        moveComponent(layout, targetLayout.getId(), location);
        events.publish(new DashboardRefreshEvent(getDashboard().getVisualModel()));
        events.publish(new WidgetSelectedEvent(layout.getId(), WidgetSelectedEvent.Target.CANVAS));
    }

    public void moveComponent(DashboardLayout layout, UUID targetLayoutId, WidgetDropLocation location) {
        RootLayout dashboardModel = getDashboard().getVisualModel();
        DashboardLayout target = findLayout(dashboardModel, targetLayoutId);
        DashboardLayout parent = findParentLayout(dashboardModel, layout);

        if (!applyMoveAction(layout, target, parent, dashboardModel)) {
            return;
        }

        if (location == null) {
            if (target.equals(parent)) {
                return;
            }
            location = WidgetDropLocation.MIDDLE;
            if (parent.equals(target.getParent()) && target instanceof WidgetLayout) {
                Integer targetIndex = parent.getChildren().indexOf(target);
                Integer sourceIndex = parent.getChildren().indexOf(layout);
                if (sourceIndex - targetIndex == 1) {
                    location = WidgetDropLocation.LEFT;
                }
            }
        }

        parent.removeOwnChild(layout);

        if (target instanceof ContainerLayout) {
            switch (location) {
                case MIDDLE:
                case CENTER:
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
            DashboardLayout targetParent = findParentLayout(dashboardModel, target);
            for (DashboardLayout childLayout : targetParent.getChildren()) {
                if (childLayout.getId().equals(target.getId())) {
                    switch (location) {
                        case TOP:
                        case LEFT:
                            newChildren.add(layout);
                            newChildren.add(childLayout);
                            break;
                        case MIDDLE:
                        case CENTER:
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
            targetParent.setChildren(newChildren);
        }
    }

    public AbstractFrame getFrame() {
        return (AbstractFrame) frame.getFrame();
    }

    private boolean applyMoveAction(DashboardLayout layout, DashboardLayout target, DashboardLayout parent, DashboardLayout dashboardModel) {
        if (target.getId().equals(layout.getId())) {
            return false;
        }

        List<DashboardLayout> targetParents = findParentsLayout(dashboardModel, target.getUuid());
        if (targetParents.contains(layout)) {
            return false;
        }

        if (layout instanceof GridCellLayout) {
            return false;
        }
        return true;
    }
}
