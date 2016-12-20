/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.drophandlers;

import com.audimex.dashboard.entity.ComponentType;
import com.audimex.dashboard.entity.DropTarget;
import com.audimex.dashboard.web.ComponentDescriptor;
import com.audimex.dashboard.web.utils.DashboardUtils;
import com.audimex.dashboard.web.widgets.WidgetPanel;
import com.haulmont.bali.datastruct.Node;
import com.haulmont.bali.datastruct.Tree;
import com.haulmont.cuba.gui.components.BoxLayout;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.shared.ui.dd.HorizontalDropLocation;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import fi.jasoft.dragdroplayouts.DDGridLayout;
import fi.jasoft.dragdroplayouts.DDHorizontalLayout;
import fi.jasoft.dragdroplayouts.DDVerticalLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;
import fi.jasoft.dragdroplayouts.drophandlers.DefaultHorizontalLayoutDropHandler;
import fi.jasoft.dragdroplayouts.events.LayoutBoundTransferable;

import java.util.Collections;
import java.util.List;

public class DDHorizontalLayoutDropHandler extends DefaultHorizontalLayoutDropHandler {
    protected StructureChangeListener structureChangeListener;
    protected GridDropListener gridDropListener;
    protected Tree<ComponentDescriptor> componentDescriptorTree;

    private BoxLayout dashboardContainer;

    public DDHorizontalLayoutDropHandler(BoxLayout dashboardContainer) {
        this.dashboardContainer = dashboardContainer;
    }

    @Override
    protected void handleComponentReordering(DragAndDropEvent event) {
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event
                .getTransferable();
        DDHorizontalLayout.HorizontalLayoutTargetDetails details = (DDHorizontalLayout.HorizontalLayoutTargetDetails) event
                .getTargetDetails();
        AbstractOrderedLayout layout = (AbstractOrderedLayout) details
                .getTarget();
        Component comp = transferable.getComponent();
        int oldIndex = layout.getComponentIndex(comp);

        super.handleComponentReordering(event);

        reorderComponent(componentDescriptorTree.getRootNodes(), comp, details.getOverIndex(), oldIndex);
        structureChangeListener.structureChanged(componentDescriptorTree, DropTarget.LAYOUT);
    }

    protected void reorderComponent(List<Node<ComponentDescriptor>> nodeList, Component component, int idx, int oldIdx) {
        for (Node<ComponentDescriptor> node : nodeList) {
            if (node.getData().getOwnComponent() == component) {
                Collections.swap(nodeList, idx, oldIdx);
                return;
            }

            if (node.getChildren().size() > 0) {
                reorderComponent(node.getChildren(), component, idx, oldIdx);
            }
        }
    }

    @Override
    protected void handleDropFromLayout(DragAndDropEvent event) {
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event.getTransferable();
        DDHorizontalLayout.HorizontalLayoutTargetDetails details =
                (DDHorizontalLayout.HorizontalLayoutTargetDetails) event.getTargetDetails();
        AbstractOrderedLayout targetLayout = (AbstractOrderedLayout) details.getTarget();

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
            Button dragComponent = (Button) transferable.getComponent();
            // Add component
            if (dragComponent.getId().equals("verticalLayout")) {
                comp = new DDVerticalLayout();
                comp.setSizeFull();
                comp.setStyleName("dd-bordering");

                DDVerticalLayout ddVerticalLayout = (DDVerticalLayout) comp;
                ddVerticalLayout.setDragMode(DashboardUtils.getDefaultDragMode());
                ddVerticalLayout.setSpacing(true);
                ddVerticalLayout.setMargin(true);

                DDVerticalLayoutDropHandler ddVerticalLayoutDropHandler = new DDVerticalLayoutDropHandler(dashboardContainer);
                ddVerticalLayoutDropHandler.setStructureChangeListener(structureChangeListener);
                ddVerticalLayoutDropHandler.setGridDropListener(gridDropListener);
                ddVerticalLayoutDropHandler.setComponentDescriptorTree(componentDescriptorTree);
                ddVerticalLayout.setDropHandler(ddVerticalLayoutDropHandler);
            } else if (dragComponent.getId().equals("horizontalLayout")) {
                comp = new DDHorizontalLayout();
                comp.setSizeFull();
                comp.setStyleName("dd-bordering");

                DDHorizontalLayout ddHorizontalLayout = (DDHorizontalLayout) comp;
                ddHorizontalLayout.setDragMode(DashboardUtils.getDefaultDragMode());
                ddHorizontalLayout.setSpacing(true);
                ddHorizontalLayout.setMargin(true);

                DDHorizontalLayoutDropHandler ddHorizontalLayoutDropHandler = DDHorizontalLayoutDropHandler.this;
                ddHorizontalLayoutDropHandler.setComponentDescriptorTree(componentDescriptorTree);
                ddHorizontalLayoutDropHandler.setGridDropListener(gridDropListener);
                ddHorizontalLayoutDropHandler.setStructureChangeListener(structureChangeListener);
                ddHorizontalLayout.setDropHandler(ddHorizontalLayoutDropHandler);
            } else if (dragComponent.getId().equals("gridLayout")) {
                comp = new DDGridLayout();
                comp.setSizeFull();
                comp.setStyleName("dd-bordering");

                DDGridLayout gridLayout = (DDGridLayout) comp;
                gridLayout.setSpacing(true);
                gridLayout.setMargin(true);
                gridLayout.setColumns(2);
                gridLayout.setRows(2);
                gridLayout.setDragMode(DashboardUtils.getDefaultDragMode());

                DDGridLayoutDropHandler ddGridLayoutDropHandler = new DDGridLayoutDropHandler(dashboardContainer);
                ddGridLayoutDropHandler.setComponentDescriptorTree(componentDescriptorTree);
                ddGridLayoutDropHandler.setStructureChangeListener(structureChangeListener);
                ddGridLayoutDropHandler.setGridDropListener(gridDropListener);
                gridLayout.setDropHandler(ddGridLayoutDropHandler);

                gridDropListener.gridDropped(gridLayout);
            } else if (dragComponent.getId().equals("widgetPanel")) {
                comp = new WidgetPanel(dashboardContainer);
                comp.setSizeFull();
            }

            if (idx >= 0) {
                insertComponent(componentDescriptorTree.getRootNodes(), details.getTarget(), comp, idx);
                targetLayout.addComponent(comp, idx);
            } else {
                insertComponent(componentDescriptorTree.getRootNodes(), details.getTarget(), comp, 0);
                targetLayout.addComponent(comp);
            }

            targetLayout.setExpandRatio(comp, 1);

            structureChangeListener.structureChanged(componentDescriptorTree, DropTarget.LAYOUT);
        } else {
            targetLayout.addComponent(transferable.getComponent());

            targetLayout.setExpandRatio(transferable.getComponent(), 1);
        }
    }

    protected void removeComponent(List<Node<ComponentDescriptor>> nodeList, Component componentToRemove) {
        for (Node<ComponentDescriptor> node : nodeList) {
            Component component = node.getData().getOwnComponent();
            if (component == componentToRemove) {
                node.getParent().removeChildAt(node.getNumberOfChildren());
                return;
            }

            if (node.getChildren().size() > 0) {
                removeComponent(node.getChildren(), componentToRemove);
            }
        }
    }

    protected void insertComponent(List<Node<ComponentDescriptor>> nodeList, Component parentId, Component newComponent, int idx) {
        for (Node<ComponentDescriptor> node : nodeList) {
            Component component = node.getData().getOwnComponent();
            if (component == parentId) {
                if (component instanceof AbstractOrderedLayout) {
                    List<Node<ComponentDescriptor>> childList = node.getChildren();

                    ComponentType componentType = DashboardUtils.getTypeByComponent(newComponent);

                    childList.add(idx, new Node<>(new ComponentDescriptor(newComponent, componentType)));
                    node.setChildren(childList);
                    return;
                }
            }

            if (node.getChildren().size() > 0) {
                insertComponent(node.getChildren(), parentId, newComponent, idx);
            }
        }
    }

    protected void setStructureChangeListener(StructureChangeListener structureChangeListener) {
        this.structureChangeListener = structureChangeListener;
    }

    public void setComponentDescriptorTree(Tree<ComponentDescriptor> componentDescriptorTree) {
        this.componentDescriptorTree = componentDescriptorTree;
    }

    public void setGridDropListener(GridDropListener gridDropListener) {
        this.gridDropListener = gridDropListener;
    }
}
