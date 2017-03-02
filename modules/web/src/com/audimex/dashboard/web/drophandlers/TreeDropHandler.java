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
import com.audimex.dashboard.web.widgets.GridRow;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.ServerSideCriterion;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.ui.*;
import fi.jasoft.dragdroplayouts.events.LayoutBoundTransferable;

import java.util.function.Consumer;

public class TreeDropHandler implements DropHandler {
    private Tree componentDescriptorTree;
    private GridDropListener gridDropListener;

    private Consumer<Tree> treeChangeListener;

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

                if (dragComponent.getWidgetType() == WidgetType.VERTICAL_LAYOUT) {
                    component = LayoutUtils.createVerticalDropLayout(componentDescriptorTree, gridDropListener);
                    ((DashboardVerticalLayout) component).setParentFrame(dragComponent.getDropFrame());
                } else if (dragComponent.getWidgetType() == WidgetType.HORIZONTAL_LAYOUT) {
                    component = LayoutUtils.createHorizontalDropLayout(componentDescriptorTree, gridDropListener);
                    ((DashboardHorizontalLayout) component).setParentFrame(dragComponent.getDropFrame());
                } else if (dragComponent.getWidgetType() == WidgetType.GRID_LAYOUT) {
                    component = LayoutUtils.createGridDropLayout(componentDescriptorTree, gridDropListener);
                    gridDropListener.gridDropped((GridLayout) component);
                } else if (dragComponent.getWidgetType() == WidgetType.FRAME_PANEL) {
                    component = new FramePanel(componentDescriptorTree);
                    ((FramePanel) component).setParentFrame(dragComponent.getDropFrame());
                    ((FramePanel) component).setContent(dragComponent.getWidget().getFrameId());
                } else {
                    component = new Label();
                }

                if (component instanceof LayoutEvents.LayoutClickNotifier) {
                    ((LayoutEvents.LayoutClickNotifier) component).addLayoutClickListener(
                            (LayoutEvents.LayoutClickListener) event1 ->
                                    componentDescriptorTree.setValue(event1.getChildComponent())
                    );
                }

                Object parentId = tree.getParent(target.getItemIdOver());
                if (location == VerticalDropLocation.MIDDLE) {
                    TreeUtils.addComponent(componentDescriptorTree, (Component) target.getItemIdOver(), component, 0);
                } else if (location == VerticalDropLocation.BOTTOM) {
                    TreeUtils.addComponent(componentDescriptorTree, (Component) parentId, component, 0);
                } else {
                    TreeUtils.addComponent(componentDescriptorTree, (Component) parentId, component, 0);
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
                TreeUtils.moveComponent(tree, targetItemId, sourceItemId, 0);
            } else if (location == VerticalDropLocation.TOP) {
                int position = TreeUtils.calculatePosition(tree, targetItemId);
                TreeUtils.moveComponent(tree, tree.getParent(targetItemId), sourceItemId, position);
            } else if (location == VerticalDropLocation.BOTTOM) {
                int position = TreeUtils.calculatePosition(tree, targetItemId) + 1;
                TreeUtils.moveComponent(tree, tree.getParent(targetItemId), sourceItemId, position);
            }
        }

        treeChangeListener.accept(tree);
    }

    public void setTreeChangeListener(Consumer<Tree> treeChangeListener) {
        this.treeChangeListener = treeChangeListener;
    }

    public Consumer<Tree> getTreeChangeListener() {
        return treeChangeListener;
    }

    @Override
    public AcceptCriterion getAcceptCriterion() {
        ServerSideCriterion serverSideCriterion = new ServerSideCriterion() {
            @Override
            public boolean accept(DragAndDropEvent dragEvent) {
                com.vaadin.ui.Tree.TreeTargetDetails targetDetails =
                        (com.vaadin.ui.Tree.TreeTargetDetails) dragEvent.getTargetDetails();
                VerticalDropLocation location = targetDetails.getDropLocation();
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

    public void setComponentDescriptorTree(Tree componentDescriptorTree) {
        this.componentDescriptorTree = componentDescriptorTree;
    }

    public void setGridDropListener(GridDropListener gridDropListener) {
        this.gridDropListener = gridDropListener;
    }

    public GridDropListener getGridDropListener() {
        return gridDropListener;
    }
}
