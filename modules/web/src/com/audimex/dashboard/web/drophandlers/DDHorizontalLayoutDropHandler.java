/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.drophandlers;

import com.audimex.dashboard.entity.WidgetType;
import com.audimex.dashboard.web.layouts.DashboardHorizontalLayout;
import com.audimex.dashboard.web.palette.PaletteButton;
import com.audimex.dashboard.web.utils.LayoutUtils;
import com.audimex.dashboard.web.utils.TreeUtils;
import com.audimex.dashboard.web.widgets.FramePanel;
import com.haulmont.cuba.gui.components.Frame;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.ServerSideCriterion;
import com.vaadin.shared.ui.dd.HorizontalDropLocation;
import com.vaadin.ui.*;
import fi.jasoft.dragdroplayouts.DDHorizontalLayout;
import fi.jasoft.dragdroplayouts.drophandlers.DefaultHorizontalLayoutDropHandler;
import fi.jasoft.dragdroplayouts.events.LayoutBoundTransferable;

public class DDHorizontalLayoutDropHandler extends DefaultHorizontalLayoutDropHandler {
    private GridDropListener gridDropListener;
    private Tree tree;
    private Frame frame;

    @Override
    protected void handleDropFromLayout(DragAndDropEvent event) {
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event.getTransferable();
        DDHorizontalLayout.HorizontalLayoutTargetDetails details =
                (DDHorizontalLayout.HorizontalLayoutTargetDetails) event.getTargetDetails();
        AbstractLayout targetLayout = (AbstractLayout) details.getTarget();

        if (targetLayout.getParent() instanceof DashboardHorizontalLayout) {
            targetLayout = (DashboardHorizontalLayout) targetLayout.getParent();
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

        HorizontalDropLocation loc = (details).getDropLocation();
        if (loc == HorizontalDropLocation.CENTER
                || loc == HorizontalDropLocation.RIGHT) {
            idx++;
        }

        if (transferable.getComponent() instanceof Button) {
            PaletteButton dragComponent = (PaletteButton) transferable.getComponent();
            // Add component
            if (dragComponent.getWidgetType() == WidgetType.VERTICAL_LAYOUT) {
                comp = LayoutUtils.createVerticalDropLayout(tree, gridDropListener, frame);
            } else if (dragComponent.getWidgetType() == WidgetType.HORIZONTAL_LAYOUT) {
                comp = LayoutUtils.createHorizontalDropLayout(tree, gridDropListener, frame);
            } else if (dragComponent.getWidgetType() == WidgetType.GRID_LAYOUT) {
                comp = LayoutUtils.createGridDropLayout(tree, gridDropListener, frame);
                gridDropListener.gridDropped((GridLayout) comp, targetLayout, idx);
            } else if (dragComponent.getWidgetType() == WidgetType.FRAME_PANEL) {
                comp = new FramePanel(tree, dragComponent.getWidget().getFrameId(), frame);
            }

            if (dragComponent.getWidgetType() != WidgetType.GRID_LAYOUT) {
                if (idx >= 0) {
                    TreeUtils.addComponent(tree, targetLayout, comp, idx);
                } else {
                    TreeUtils.addComponent(tree, targetLayout, comp, 0);
                }
            }
        } else {
            if (comp == targetLayout) {
                return;
            }

            if (idx >= 0) {
                if (targetLayout instanceof DashboardHorizontalLayout) {
                    ((DashboardHorizontalLayout) targetLayout).addComponent(comp, idx);
                } else if (targetLayout instanceof AbstractOrderedLayout) {
                    ((AbstractOrderedLayout) targetLayout).addComponent(comp, idx);
                }
            } else {
                targetLayout.addComponent(comp);
            }

            TreeUtils.reorder(tree, details.getTarget(), comp, 0);

            if (targetLayout instanceof DashboardHorizontalLayout) {
                ((DashboardHorizontalLayout) targetLayout).getMainLayout().setExpandRatio(comp, 1);
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

    public Frame getFrame() {
        return frame;
    }

    public void setFrame(Frame frame) {
        this.frame = frame;
    }

    public void setTree(Tree tree) {
        this.tree = tree;
    }

    public void setGridDropListener(GridDropListener gridDropListener) {
        this.gridDropListener = gridDropListener;
    }
}
