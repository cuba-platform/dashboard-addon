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
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import fi.jasoft.dragdroplayouts.DDGridLayout;
import fi.jasoft.dragdroplayouts.DDHorizontalLayout;
import fi.jasoft.dragdroplayouts.DDVerticalLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;
import fi.jasoft.dragdroplayouts.drophandlers.DefaultGridLayoutDropHandler;
import fi.jasoft.dragdroplayouts.events.LayoutBoundTransferable;

import java.util.List;

public class DDGridLayoutDropHandler extends DefaultGridLayoutDropHandler {
    protected StructureChangeListener structureChangeListener;
    protected GridDropListener gridDropListener;
    protected Tree<ComponentDescriptor> componentDescriptorTree;

    //todo
    @Override
    protected void handleComponentReordering(DragAndDropEvent event) {
        DDGridLayout.GridLayoutTargetDetails details = (DDGridLayout.GridLayoutTargetDetails) event
                .getTargetDetails();
        DDGridLayout layout = (DDGridLayout) details.getTarget();
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event
                .getTransferable();
        Component comp = transferable.getComponent();

        int row = details.getOverRow();
        int column = details.getOverColumn();

        super.handleComponentReordering(event);

        reorderComponent(componentDescriptorTree.getRootNodes(), comp, column, row);
        structureChangeListener.structureChanged(componentDescriptorTree, DropTarget.LAYOUT);
    }

    protected void reorderComponent(List<Node<ComponentDescriptor>> nodeList, Component component, int column, int row) {
        for (Node<ComponentDescriptor> node : nodeList) {
            if (node.getData().getOwnComponent() == component) {
                int oldColumn = node.getData().getColumn();
                int oldRow = node.getData().getRow();

//                Collections.swap(nodeList, idx, oldIdx);
                return;
            }

            if (node.getChildren().size() > 0) {
                reorderComponent(node.getChildren(), component, column, row);
            }
        }
    }

    @Override
    protected void handleDropFromLayout(DragAndDropEvent event) {
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event
                .getTransferable();
        DDGridLayout.GridLayoutTargetDetails details = (DDGridLayout.GridLayoutTargetDetails) event
                .getTargetDetails();
        DDGridLayout layout = (DDGridLayout) details.getTarget();

        int row = details.getOverRow();
        int column = details.getOverColumn();
        Component comp = transferable.getComponent();

        Component parent = layout.getParent();
        while (parent != null) {
            if (parent == comp) {
                return;
            }
            parent = parent.getParent();
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
                DDVerticalLayoutDropHandler ddVerticalLayoutDropHandler = new DDVerticalLayoutDropHandler();
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
                ddHorizontalLayoutDropHandler.setStructureChangeListener(structureChangeListener);
                ddHorizontalLayout.setDropHandler(ddHorizontalLayoutDropHandler);
            } else if (dragComponent.getId().equals("gridLayout")) {
                comp = new DDGridLayout();
                comp.setSizeFull();
                comp.setStyleName("dd-bordering");

                DDGridLayout gridLayout = (DDGridLayout) comp;
                gridLayout.setColumns(1);
                gridLayout.setRows(1);

                gridLayout.setDragMode(LayoutDragMode.CLONE);
                DDGridLayoutDropHandler ddGridLayoutDropHandler = DDGridLayoutDropHandler.this;
                ddGridLayoutDropHandler.setComponentDescriptorTree(componentDescriptorTree);
                ddGridLayoutDropHandler.setStructureChangeListener(structureChangeListener);
                ddGridLayoutDropHandler.setGridDropListener(gridDropListener);
                gridLayout.setDropHandler(ddGridLayoutDropHandler);

                gridDropListener.gridDropped(gridLayout);
            } else if (dragComponent.getId().equals("widgetPanel")) {
                comp = new WidgetPanel("Panel");
                comp.setSizeFull();
            }

            insertComponent(componentDescriptorTree.getRootNodes(), details.getTarget().toString(), comp, 0);
            addComponent(event, comp, column, row);

            structureChangeListener.structureChanged(componentDescriptorTree, DropTarget.LAYOUT);
        } else {
            removeComponent(componentDescriptorTree.getRootNodes(), comp);

            insertComponent(componentDescriptorTree.getRootNodes(), details.getTarget().toString(), comp, 0);
            addComponent(event, comp, column, row);

            structureChangeListener.structureChanged(componentDescriptorTree, DropTarget.LAYOUT);
        }
    }

    protected void removeComponent(List<Node<ComponentDescriptor>> nodeList, Component componentToRemove) {
        for (Node node : nodeList) {
            Component component = ((ComponentDescriptor) node.getData()).getOwnComponent();
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
                if (component instanceof AbstractLayout) {
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
                    childList.add(new Node<>(new ComponentDescriptor(newComponent, componentType)));
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
