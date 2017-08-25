/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.drophandlers;

import com.audimex.dashboard.entity.Dashboard;
import com.audimex.dashboard.entity.WidgetType;
import com.audimex.dashboard.web.dashboard.PaletteButton;
import com.audimex.dashboard.web.layouts.DashboardGridLayout;
import com.audimex.dashboard.web.tools.DashboardTools;
import com.audimex.dashboard.web.tools.DashboardWidgetsFactory;
import com.audimex.dashboard.web.widgets.FramePanel;
import com.audimex.dashboard.web.widgets.GridCell;
import com.audimex.dashboard.web.widgets.GridRow;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.components.Frame;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.ServerSideCriterion;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Tree;
import fi.jasoft.dragdroplayouts.events.LayoutBoundTransferable;

import java.util.function.Consumer;

public class TreeDropHandler implements DropHandler {
    protected Tree tree;
    protected Dashboard dashboard;
    protected GridDropListener gridDropListener;
    protected Frame frame;
    protected Consumer<Tree> treeHandler;

    protected DashboardTools dashboardTools = AppBeans.get(DashboardTools.NAME);
    protected DashboardWidgetsFactory dashboardWidgetsFactory = AppBeans.get(DashboardWidgetsFactory.NAME);

    @Override
    public void drop(DragAndDropEvent event) {
        com.vaadin.ui.Tree.TreeTargetDetails target = (com.vaadin.ui.Tree.TreeTargetDetails) event.getTargetDetails();
        VerticalDropLocation location = target.getDropLocation();
        com.vaadin.ui.Tree tree = target.getTarget();

        if (event.getTransferable() instanceof LayoutBoundTransferable) {
            LayoutBoundTransferable transferable = (LayoutBoundTransferable) event.getTransferable();

            if (transferable.getComponent() instanceof Button) {
                PaletteButton dragComponent = (PaletteButton) transferable.getComponent();
                Component component;

                if (location == VerticalDropLocation.MIDDLE) {
                    component = dashboardWidgetsFactory.createWidgetOnDrop(dragComponent, tree, gridDropListener,
                            frame, treeHandler, target.getItemIdOver());
                } else {
                    component = dashboardWidgetsFactory.createWidgetOnDrop(dragComponent, tree, gridDropListener,
                            frame, treeHandler, tree.getParent(target.getItemIdOver()));
                }

                if (dragComponent.getWidgetType() != WidgetType.GRID_LAYOUT) {
                    Object parentId = tree.getParent(target.getItemIdOver());
                    if (location == VerticalDropLocation.MIDDLE) {
                        dashboardTools.addComponent(this.tree, target.getItemIdOver(), component, 0);
                    } else if (location == VerticalDropLocation.BOTTOM) {
                        dashboardTools.addComponent(this.tree, parentId, component, 0);
                    } else {
                        dashboardTools.addComponent(this.tree, parentId, component, 0);
                    }
                }
            }
        } else {
            Transferable t = event.getTransferable();

            if (t.getSourceComponent() != tree) {
                return;
            }

            Object sourceItemId = t.getData("itemId");
            Object targetItemId = target.getItemIdOver();

            if (location == VerticalDropLocation.MIDDLE) {
                dashboardTools.moveComponent(tree, targetItemId, sourceItemId, 0);
            } else if (location == VerticalDropLocation.TOP) {
                int position = dashboardTools.calculatePosition(tree, targetItemId);
                dashboardTools.moveComponent(tree, tree.getParent(targetItemId), sourceItemId, position);
            } else if (location == VerticalDropLocation.BOTTOM) {
                int position = dashboardTools.calculatePosition(tree, targetItemId) + 1;
                dashboardTools.moveComponent(tree, tree.getParent(targetItemId), sourceItemId, position);
            }

            if (targetItemId instanceof GridCell) {
                GridCell gridCell = (GridCell) targetItemId;
                dashboardTools.markGridCells(
                        tree,
                        (DashboardGridLayout) gridCell.getParent().getParent(),
                        gridCell.getRow(),
                        gridCell.getColumn(),
                        1,
                        1);
            }
        }

        treeHandler.accept(tree);
    }

    public Frame getFrame() {
        return frame;
    }

    public void setFrame(Frame frame) {
        this.frame = frame;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }


    public void setTreeHandler(Consumer<Tree> treeHandler) {
        this.treeHandler = treeHandler;
    }

    public Consumer<Tree> getTreeHandler() {
        return treeHandler;
    }

    @Override
    public AcceptCriterion getAcceptCriterion() {
        ServerSideCriterion serverSideCriterion = new ServerSideCriterion() {
            @Override
            public boolean accept(DragAndDropEvent dragEvent) {
                com.vaadin.ui.Tree.TreeTargetDetails targetDetails =
                        (com.vaadin.ui.Tree.TreeTargetDetails) dragEvent.getTargetDetails();
                VerticalDropLocation location = targetDetails.getDropLocation();
                Object draggedComponent = dragEvent.getTransferable().getData("itemId");
                if (draggedComponent instanceof GridCell
                        || draggedComponent instanceof GridRow) {
                    return false;
                }
                if (targetDetails.getItemIdOver() == null) {
                    return false;
                }
                if (targetDetails.getItemIdOver() instanceof FramePanel) {
                    if (location == VerticalDropLocation.MIDDLE) {
                        return false;
                    } else {
                        return true;
                    }
                }
                if (targetDetails.getItemIdInto() != null
                        && targetDetails.getItemIdInto() instanceof GridLayout) {
                    return false;
                }
                if (targetDetails.getItemIdInto() != null
                        && targetDetails.getItemIdInto() instanceof GridRow) {
                    if (targetDetails.getItemIdOver() != null
                            && targetDetails.getItemIdOver() instanceof GridCell) {
                        if (location == VerticalDropLocation.MIDDLE) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
                if (targetDetails.getTarget().getParent(targetDetails.getItemIdOver()) == null
                        && location != VerticalDropLocation.MIDDLE) {
                    return false;
                }
                if (targetDetails.getTarget().getParent(targetDetails.getItemIdAfter()) == null
                        && location != VerticalDropLocation.MIDDLE) {
                    return false;
                }
                return true;
            }
        };
        return serverSideCriterion;
    }

    public void setTree(Tree tree) {
        this.tree = tree;
    }

    public void setGridDropListener(GridDropListener gridDropListener) {
        this.gridDropListener = gridDropListener;
    }

    public GridDropListener getGridDropListener() {
        return gridDropListener;
    }
}
