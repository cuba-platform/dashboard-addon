/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.drophandlers;

import com.audimex.dashboard.entity.WidgetType;
import com.audimex.dashboard.web.palette.PaletteButton;
import com.audimex.dashboard.web.utils.LayoutUtils;
import com.audimex.dashboard.web.utils.TreeUtils;
import com.audimex.dashboard.web.widgets.FramePanel;
import com.audimex.dashboard.web.widgets.GridCell;
import com.haulmont.cuba.gui.components.Frame;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.ServerSideCriterion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Tree;
import fi.jasoft.dragdroplayouts.DDGridLayout;
import fi.jasoft.dragdroplayouts.drophandlers.DefaultGridLayoutDropHandler;
import fi.jasoft.dragdroplayouts.events.LayoutBoundTransferable;

public class DDGridLayoutDropHandler extends DefaultGridLayoutDropHandler {
    private GridDropListener gridDropListener;
    private Tree tree;
    private Frame frame;

    @Override
    protected void handleDropFromLayout(DragAndDropEvent event) {
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event.getTransferable();
        DDGridLayout.GridLayoutTargetDetails details = (DDGridLayout.GridLayoutTargetDetails) event.getTargetDetails();
        DDGridLayout layout = (DDGridLayout) details.getTarget();

        int row = details.getOverRow();
        int column = details.getOverColumn();
        Component component = transferable.getComponent();

        Component parent = layout.getParent();
        while (parent != null) {
            if (parent == component) {
                return;
            }
            parent = parent.getParent();
        }

        if (transferable.getComponent() instanceof Button) {
            PaletteButton dragComponent = (PaletteButton) transferable.getComponent();
            // Add component
            if (dragComponent.getWidgetType() == WidgetType.VERTICAL_LAYOUT) {
                component = LayoutUtils.createVerticalDropLayout(tree, gridDropListener, frame);
            } else if (dragComponent.getWidgetType() == WidgetType.HORIZONTAL_LAYOUT) {
                component = LayoutUtils.createHorizontalDropLayout(tree, gridDropListener, frame);
            } else if (dragComponent.getWidgetType() == WidgetType.GRID_LAYOUT) {
                component = LayoutUtils.createGridDropLayout(tree, gridDropListener, frame);
                gridDropListener.gridDropped((GridLayout) component, layout, 0);
            } else if (dragComponent.getWidgetType() == WidgetType.FRAME_PANEL) {
                component = new FramePanel(tree, dragComponent.getWidget().getFrameId(), frame);
            }

            if (dragComponent.getWidgetType() != WidgetType.GRID_LAYOUT) {
                Component target = null;
                if (details.getTarget() instanceof GridLayout) {
                    GridLayout gridLayout = (GridLayout) details.getTarget();
                    target = LayoutUtils.getGridCell(
                            tree,
                            tree.getChildren(gridLayout),
                            column,
                            row);
                } else {
                    target = details.getTarget();
                }
                TreeUtils.addComponent(tree, target, component, 0);
            }
        } else {
            TreeUtils.addComponent(tree, details.getTarget(), component, 0);
        }


        ((TreeDropHandler) tree.getDropHandler()).getTreeChangeListener().accept(tree);
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

    public Frame getFrame() {
        return frame;
    }

    public void setFrame(Frame frame) {
        this.frame = frame;
    }

    @Override
    public AcceptCriterion getAcceptCriterion() {
        ServerSideCriterion serverSideCriterion = new ServerSideCriterion() {
            @Override
            public boolean accept(DragAndDropEvent dragEvent) {
                DDGridLayout.GridLayoutTargetDetails targetDetails =
                        (DDGridLayout.GridLayoutTargetDetails) dragEvent.getTargetDetails();
                Component insideComponent = ((GridLayout) targetDetails.getTarget())
                                                .getComponent(targetDetails.getOverColumn(), targetDetails.getOverRow());
                if (insideComponent != null && !(insideComponent instanceof GridCell)) {
                    return false;
                }
                return true;
            }
        };
        return serverSideCriterion;
    }
}
