/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.drophandlers;

import com.audimex.dashboard.entity.ComponentType;
import com.audimex.dashboard.entity.DropTarget;
import com.audimex.dashboard.web.ComponentDescriptor;
import com.audimex.dashboard.web.WidgetPanel;
import com.haulmont.bali.datastruct.Node;
import com.haulmont.bali.datastruct.Tree;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import fi.jasoft.dragdroplayouts.DDGridLayout;
import fi.jasoft.dragdroplayouts.DDHorizontalLayout;
import fi.jasoft.dragdroplayouts.DDVerticalLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;
import fi.jasoft.dragdroplayouts.drophandlers.DefaultVerticalLayoutDropHandler;
import fi.jasoft.dragdroplayouts.events.LayoutBoundTransferable;

import java.util.Collections;
import java.util.List;

public class DDVerticalLayoutDropHandler extends DefaultVerticalLayoutDropHandler {
    protected StructureChangeListener structureChangeListener;
    protected GridDropListener gridDropListener;
    protected Tree<ComponentDescriptor> componentDescriptorTree;

    @Override
    protected void handleComponentReordering(DragAndDropEvent event) {
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event
                .getTransferable();
        DDVerticalLayout.VerticalLayoutTargetDetails details = (DDVerticalLayout.VerticalLayoutTargetDetails) event
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
        DDVerticalLayout.VerticalLayoutTargetDetails details =
                (DDVerticalLayout.VerticalLayoutTargetDetails) event.getTargetDetails();
        AbstractOrderedLayout layout =
                (AbstractOrderedLayout) details.getTarget();

        int idx = (details).getOverIndex();
        Component comp = transferable.getComponent();

        Component parent = layout.getParent();
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
            Button dragComponent = (Button) transferable.getComponent();
            // Add component
            if (dragComponent.getId().equals("verticalLayout")) {
                comp = new DDVerticalLayout();
                comp.setSizeFull();
                comp.setStyleName("dd-bordering");

                DDVerticalLayout ddVerticalLayout = (DDVerticalLayout) comp;
                ddVerticalLayout.setDragMode(LayoutDragMode.CLONE);
                ddVerticalLayout.setSpacing(true);
                ddVerticalLayout.setMargin(true);
                DDVerticalLayoutDropHandler ddVerticalLayoutDropHandler = DDVerticalLayoutDropHandler.this;
                ddVerticalLayoutDropHandler.setStructureChangeListener(structureChangeListener);
                ddVerticalLayoutDropHandler.setGridDropListener(gridDropListener);
                ddVerticalLayoutDropHandler.setComponentDescriptorTree(componentDescriptorTree);
                ddVerticalLayout.setDropHandler(ddVerticalLayoutDropHandler);
            } else if (dragComponent.getId().equals("horizontalLayout")) {
                comp = new DDHorizontalLayout();
                comp.setSizeFull();
                comp.setStyleName("dd-bordering");

                DDHorizontalLayout ddHorizontalLayout = (DDHorizontalLayout) comp;
                ddHorizontalLayout.setDragMode(LayoutDragMode.CLONE);
                ddHorizontalLayout.setSpacing(true);
                ddHorizontalLayout.setMargin(true);
                DDHorizontalLayoutDropHandler ddHorizontalLayoutDropHandler = new DDHorizontalLayoutDropHandler();
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
                gridLayout.setColumns(1);
                gridLayout.setRows(1);
                gridLayout.setDragMode(LayoutDragMode.CLONE);

                DDGridLayoutDropHandler ddGridLayoutDropHandler = new DDGridLayoutDropHandler();
                ddGridLayoutDropHandler.setComponentDescriptorTree(componentDescriptorTree);
                ddGridLayoutDropHandler.setStructureChangeListener(structureChangeListener);
                ddGridLayoutDropHandler.setGridDropListener(gridDropListener);
                gridLayout.setDropHandler(ddGridLayoutDropHandler);

                gridDropListener.gridDropped(gridLayout);
            } else if (dragComponent.getId().equals("widgetPanel")) {
                comp = new WidgetPanel("Panel");
                comp.setSizeFull();
            }


            if (idx >= 0) {
                insertComponent(componentDescriptorTree.getRootNodes(), details.getTarget().toString(), comp, idx);
                ((AbstractOrderedLayout) details.getTarget()).addComponent(comp, idx);
            } else {
                insertComponent(componentDescriptorTree.getRootNodes(), details.getTarget().toString(), comp, 0);
                ((AbstractOrderedLayout) details.getTarget()).addComponent(comp);
            }

            structureChangeListener.structureChanged(componentDescriptorTree, DropTarget.LAYOUT);
        } else {
            removeComponent(componentDescriptorTree.getRootNodes(), comp);

            if (idx >= 0) {
                insertComponent(componentDescriptorTree.getRootNodes(), details.getTarget().toString(), comp, idx);
                ((AbstractOrderedLayout) details.getTarget()).addComponent(comp, idx);
            } else {
                insertComponent(componentDescriptorTree.getRootNodes(), details.getTarget().toString(), comp, 0);
                ((AbstractOrderedLayout) details.getTarget()).addComponent(comp);
            }
            structureChangeListener.structureChanged(componentDescriptorTree, DropTarget.LAYOUT);
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

    protected void insertComponent(List<Node<ComponentDescriptor>> nodeList, String parentId, Component newComponent, int idx) {
        for (Node<ComponentDescriptor> node : nodeList) {
            Component component = node.getData().getOwnComponent();
            if (component.toString().equals(parentId)) {
                if (component instanceof AbstractOrderedLayout) {
                    List<Node<ComponentDescriptor>> childList = node.getChildren();
                    ComponentType componentType;
                    if (newComponent instanceof DDVerticalLayout) {
                        componentType = ComponentType.VERTICAL_LAYOUT;
                    } else if (newComponent instanceof DDHorizontalLayout) {
                        componentType = ComponentType.HORIZONTAL_LAYOUT;
                    } else if (newComponent instanceof DDGridLayout) {
                        componentType = ComponentType.GRID_LAYOUT;
                    } else {
                        componentType = ComponentType.WIDGET;
                    }
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

    public void setStructureChangeListener(StructureChangeListener structureChangeListener) {
        this.structureChangeListener = structureChangeListener;
    }

    public void setComponentDescriptorTree(Tree<ComponentDescriptor> componentDescriptorTree) {
        this.componentDescriptorTree = componentDescriptorTree;
    }

    public void setGridDropListener(GridDropListener gridDropListener) {
        this.gridDropListener = gridDropListener;
    }
}
