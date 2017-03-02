/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.drophandlers;

import com.audimex.dashboard.entity.WidgetType;
import com.audimex.dashboard.web.layouts.DashboardHorizontalLayout;
import com.audimex.dashboard.web.layouts.DashboardVerticalLayout;
import com.audimex.dashboard.web.palette.PaletteButton;
import com.audimex.dashboard.web.utils.LayoutUtils;
import com.audimex.dashboard.web.utils.TreeUtils;
import com.audimex.dashboard.web.widgets.FramePanel;
import com.audimex.dashboard.web.widgets.GridCell;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.ServerSideCriterion;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.ui.*;
import fi.jasoft.dragdroplayouts.DDVerticalLayout;
import fi.jasoft.dragdroplayouts.drophandlers.DefaultVerticalLayoutDropHandler;
import fi.jasoft.dragdroplayouts.events.LayoutBoundTransferable;

public class DDVerticalLayoutDropHandler extends DefaultVerticalLayoutDropHandler {
    private GridDropListener gridDropListener;
    private Tree tree;

    @Override
    protected void handleDropFromLayout(DragAndDropEvent event) {
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event.getTransferable();
        DDVerticalLayout.VerticalLayoutTargetDetails details =
                (DDVerticalLayout.VerticalLayoutTargetDetails) event.getTargetDetails();
        AbstractLayout targetLayout = (AbstractLayout) details.getTarget();

        if (targetLayout.getParent() instanceof DashboardVerticalLayout) {
            targetLayout = (DashboardVerticalLayout) targetLayout.getParent();
        }

        int idx = (details).getOverIndex();
        Component comp = transferable.getComponent();

        Component parent = targetLayout.getParent();
        while (parent != null) {
            if (parent == comp) {
                return;
            }
            parent = parent.getParent();
        }

        VerticalDropLocation loc = (details).getDropLocation();
        if (loc == VerticalDropLocation.MIDDLE
                || loc == VerticalDropLocation.BOTTOM) {
            idx++;
        }

        if (transferable.getComponent() instanceof Button) {
            PaletteButton dragComponent = (PaletteButton) transferable.getComponent();
            // Add component
            if (dragComponent.getWidgetType() == WidgetType.VERTICAL_LAYOUT) {
                comp = LayoutUtils.createVerticalDropLayout(tree, gridDropListener);
                ((DashboardVerticalLayout) comp).setParentFrame(dragComponent.getDropFrame());
            } else if (dragComponent.getWidgetType() == WidgetType.HORIZONTAL_LAYOUT) {
                comp = LayoutUtils.createHorizontalDropLayout(tree, gridDropListener);
                ((DashboardHorizontalLayout) comp).setParentFrame(dragComponent.getDropFrame());
            } else if (dragComponent.getWidgetType() == WidgetType.GRID_LAYOUT) {
                comp = LayoutUtils.createGridDropLayout(tree, gridDropListener);
                gridDropListener.gridDropped((GridLayout) comp);
            } else if (dragComponent.getWidgetType() == WidgetType.FRAME_PANEL) {
                comp = new FramePanel(tree);
                ((FramePanel) comp).setParentFrame(dragComponent.getDropFrame());
                ((FramePanel) comp).setContent(dragComponent.getWidget().getFrameId());
            }

            if (idx >= 0) {
                TreeUtils.addComponent(tree, targetLayout, comp, idx);
            } else {
                TreeUtils.addComponent(tree, targetLayout, comp, 0);
            }
        } else {
            if (comp == targetLayout) {
                return;
            }

            if (idx >= 0) {
                if (targetLayout instanceof DashboardVerticalLayout) {
                    ((DashboardVerticalLayout) targetLayout).addComponent(comp, idx);
                } else if (targetLayout instanceof AbstractOrderedLayout) {
                    ((AbstractOrderedLayout) targetLayout).addComponent(comp, idx);
                }
            } else {
                targetLayout.addComponent(comp);
            }

            TreeUtils.reorder(tree, targetLayout, comp, 0);
            if (targetLayout instanceof DashboardVerticalLayout) {
                ((DashboardVerticalLayout) targetLayout).getMainLayout().setExpandRatio(comp, 1);
            }

            if (targetLayout instanceof GridCell) {
                GridCell gridCell = (GridCell) targetLayout;
                TreeUtils.markGridCells(
                        tree,
                        (GridLayout) gridCell.getParent(),
                        gridCell.getRow(),
                        gridCell.getColumn(),
                        1,
                        1);
            }
        }

        ((TreeDropHandler) tree.getDropHandler()).getTreeChangeListener().accept(tree);
    }

    @Override
    public AcceptCriterion getAcceptCriterion() {
        ServerSideCriterion serverSideCriterion = new ServerSideCriterion() {
            @Override
            public boolean accept(DragAndDropEvent dragEvent) {
                if (dragEvent.getTransferable().getSourceComponent() instanceof Tree) {
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
}
