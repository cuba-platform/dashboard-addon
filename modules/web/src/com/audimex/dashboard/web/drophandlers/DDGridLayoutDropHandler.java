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
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.ServerSideCriterion;
import com.vaadin.ui.*;
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

    private BoxLayout dashboardContainer;

    public DDGridLayoutDropHandler(BoxLayout dashboardContainer) {
        this.dashboardContainer = dashboardContainer;
    }

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
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event.getTransferable();
        DDGridLayout.GridLayoutTargetDetails details = (DDGridLayout.GridLayoutTargetDetails) event.getTargetDetails();
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
                DDHorizontalLayoutDropHandler ddHorizontalLayoutDropHandler = new DDHorizontalLayoutDropHandler(dashboardContainer);
                ddHorizontalLayoutDropHandler.setComponentDescriptorTree(componentDescriptorTree);
                ddHorizontalLayoutDropHandler.setStructureChangeListener(structureChangeListener);
                ddHorizontalLayout.setDropHandler(ddHorizontalLayoutDropHandler);
            } else if (dragComponent.getId().equals("gridLayout")) {
                comp = new DDGridLayout();
                comp.setSizeFull();
                comp.setStyleName("dd-bordering");

                DDGridLayout gridLayout = (DDGridLayout) comp;
                gridLayout.setColumns(2);
                gridLayout.setSpacing(true);
                gridLayout.setMargin(true);
                gridLayout.setRows(2);

                gridLayout.setDragMode(DashboardUtils.getDefaultDragMode());
                DDGridLayoutDropHandler ddGridLayoutDropHandler = DDGridLayoutDropHandler.this;
                ddGridLayoutDropHandler.setComponentDescriptorTree(componentDescriptorTree);
                ddGridLayoutDropHandler.setStructureChangeListener(structureChangeListener);
                ddGridLayoutDropHandler.setGridDropListener(gridDropListener);
                gridLayout.setDropHandler(ddGridLayoutDropHandler);

                gridDropListener.gridDropped(gridLayout);
            } else if (dragComponent.getId().equals("widgetPanel")) {
                comp = new WidgetPanel(dashboardContainer);
            }

            insertComponent(componentDescriptorTree.getRootNodes(), details.getTarget(), comp, row, column);
            if (layout.getComponent(column, row) instanceof Label) {
                layout.removeComponent(column, row);
            }
            addComponent(event, comp, column, row);

            structureChangeListener.structureChanged(componentDescriptorTree, DropTarget.LAYOUT);
        } else {
            removeComponent(componentDescriptorTree.getRootNodes(), comp);

            insertComponent(componentDescriptorTree.getRootNodes(), details.getTarget(), comp, row, column);
            addComponent(event, comp, column, row);

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

    protected void insertComponent(List<Node<ComponentDescriptor>> nodeList, Component parentId, Component newComponent, int row, int column) {
        for (Node<ComponentDescriptor> node : nodeList) {
            Component component = node.getData().getOwnComponent();
            if (component == parentId) {
                if (component instanceof AbstractLayout) {
                    List<Node<ComponentDescriptor>> childList = node.getChildren();

                    ComponentType componentType = DashboardUtils.getTypeByComponent(newComponent);

                    for (Node<ComponentDescriptor> aChildList : childList) {
                        if (aChildList != null) {
                            ComponentDescriptor existingComponent = aChildList.getData();
                            if (existingComponent.getColumn() == column
                                    && existingComponent.getRow() == row) {
                                return;
                            }
                        }
                    }

                    ComponentDescriptor newComponentDescriptor = new ComponentDescriptor(newComponent, componentType);
                    newComponentDescriptor.setRow(row);
                    newComponentDescriptor.setColumn(column);
                    childList.add(new Node<>(newComponentDescriptor));
                    node.setChildren(childList);
                    return;
                }
            }

            if (node.getChildren().size() > 0) {
                insertComponent(node.getChildren(), parentId, newComponent, row, column);
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

    @Override
    public AcceptCriterion getAcceptCriterion() {
        ServerSideCriterion serverSideCriterion = new ServerSideCriterion() {
            @Override
            public boolean accept(DragAndDropEvent dragEvent) {
                DDGridLayout.GridLayoutTargetDetails targetDetails =
                        (DDGridLayout.GridLayoutTargetDetails) dragEvent.getTargetDetails();
                Component insideComponent = ((GridLayout) targetDetails.getTarget())
                                                .getComponent(targetDetails.getOverColumn(), targetDetails.getOverRow());
                if (insideComponent != null && !(insideComponent instanceof Label)) {
                    return false;
                }
                return true;
            }
        };
        return serverSideCriterion;
    }
}
