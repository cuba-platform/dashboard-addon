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
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.ui.*;
import fi.jasoft.dragdroplayouts.DDGridLayout;
import fi.jasoft.dragdroplayouts.DDHorizontalLayout;
import fi.jasoft.dragdroplayouts.DDVerticalLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;
import fi.jasoft.dragdroplayouts.events.LayoutBoundTransferable;

import java.util.List;

public class TreeDropHandler implements DropHandler {
    private Tree<ComponentDescriptor> componentDescriptorTree;
    private StructureChangeListener structureChangeListener;
    private GridDropListener gridDropListener;

    private BoxLayout dashboardContainer;

    public TreeDropHandler(BoxLayout dashboardContainer) {
        this.dashboardContainer = dashboardContainer;
    }

    @Override
    public void drop(DragAndDropEvent event) {
        com.vaadin.ui.Tree.TreeTargetDetails target = (com.vaadin.ui.Tree.TreeTargetDetails) event.getTargetDetails();
        VerticalDropLocation location = target.getDropLocation();
        com.vaadin.ui.Tree tree = target.getTarget();
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event.getTransferable();

        if (transferable.getComponent() instanceof Button) {
            Button dragComponent = (Button) transferable.getComponent();
            Component newComponent;

            if (dragComponent.getId().equals("verticalLayout")) {
                newComponent = new DDVerticalLayout();
                newComponent.setSizeFull();
                newComponent.setStyleName("dd-bordering");

                DDVerticalLayout ddVerticalLayout = (DDVerticalLayout) newComponent;
                ddVerticalLayout.setDragMode(LayoutDragMode.CLONE);
                ddVerticalLayout.setSpacing(true);
                ddVerticalLayout.setMargin(true);

                DDVerticalLayoutDropHandler ddVerticalLayoutDropHandler = new DDVerticalLayoutDropHandler(dashboardContainer);
                ddVerticalLayoutDropHandler.setStructureChangeListener(structureChangeListener);
                ddVerticalLayoutDropHandler.setGridDropListener(gridDropListener);
                ddVerticalLayoutDropHandler.setComponentDescriptorTree(componentDescriptorTree);
                ddVerticalLayout.setDropHandler(ddVerticalLayoutDropHandler);
            } else if (dragComponent.getId().equals("horizontalLayout")) {
                newComponent = new DDHorizontalLayout();
                newComponent.setSizeFull();
                newComponent.setStyleName("dd-bordering");

                DDHorizontalLayout ddHorizontalLayout = (DDHorizontalLayout) newComponent;
                ddHorizontalLayout.setDragMode(LayoutDragMode.CLONE);
                ddHorizontalLayout.setSpacing(true);
                ddHorizontalLayout.setMargin(true);

                DDHorizontalLayoutDropHandler ddHorizontalLayoutDropHandler = new DDHorizontalLayoutDropHandler(dashboardContainer);
                ddHorizontalLayoutDropHandler.setComponentDescriptorTree(componentDescriptorTree);
                ddHorizontalLayoutDropHandler.setGridDropListener(gridDropListener);
                ddHorizontalLayoutDropHandler.setStructureChangeListener(structureChangeListener);
                ddHorizontalLayout.setDropHandler(ddHorizontalLayoutDropHandler);
            } else if (dragComponent.getId().equals("gridLayout")) {
                newComponent = new DDGridLayout();
                newComponent.setSizeFull();
                newComponent.setStyleName("dd-bordering");

                DDGridLayout gridLayout = (DDGridLayout) newComponent;
                gridLayout.setSpacing(true);
                gridLayout.setMargin(true);
                gridLayout.setColumns(1);
                gridLayout.setRows(1);
                gridLayout.setDragMode(LayoutDragMode.CLONE);

                DDGridLayoutDropHandler ddGridLayoutDropHandler = new DDGridLayoutDropHandler(dashboardContainer);
                ddGridLayoutDropHandler.setComponentDescriptorTree(componentDescriptorTree);
                ddGridLayoutDropHandler.setStructureChangeListener(structureChangeListener);
                ddGridLayoutDropHandler.setGridDropListener(gridDropListener);
                gridLayout.setDropHandler(ddGridLayoutDropHandler);

                gridDropListener.gridDropped(gridLayout);
            } else if (dragComponent.getId().equals("widgetPanel")) {
                newComponent = new WidgetPanel(dashboardContainer);
            } else {
                newComponent = new Label();
            }

            if (location == VerticalDropLocation.MIDDLE) {
                insertComponent(componentDescriptorTree.getRootNodes(), ((Component) target.getItemIdOver()), newComponent, null);
                tree.addItem(newComponent);
                tree.setChildrenAllowed(target.getItemIdOver(), true);
                tree.expandItem(target.getItemIdInto());
                tree.setParent(newComponent, target.getItemIdOver());
            } else if (location == VerticalDropLocation.BOTTOM) {
                insertComponent(componentDescriptorTree.getRootNodes(), ((Component) target.getItemIdOver()), newComponent, null);
                tree.addItem(newComponent);
                tree.setParent(newComponent, target.getItemIdOver());
            } else {
                insertComponent(componentDescriptorTree.getRootNodes(), (Component)target.getItemIdInto(),
                        newComponent, ((Component) target.getItemIdOver()));

                tree.addItem(newComponent);
                tree.setParent(newComponent, target.getItemIdInto());
                HierarchicalContainer hierarchicalContainer = (HierarchicalContainer) tree.getContainerDataSource();
                hierarchicalContainer.moveAfterSibling(newComponent, target.getItemIdOver());
            }

            tree.setChildrenAllowed(newComponent, false);
            if (newComponent instanceof DDVerticalLayout) {
                tree.setItemCaption(newComponent, "Vertical");
            } else if (newComponent instanceof DDHorizontalLayout) {
                tree.setItemCaption(newComponent, "Horizontal");
            } else if (newComponent instanceof DDGridLayout) {
                tree.setItemCaption(newComponent, "Grid");
            } else {
                tree.setItemCaption(newComponent, "Widget");
            }

            structureChangeListener.structureChanged(componentDescriptorTree, DropTarget.TREE);
        }
    }

    protected void insertComponent(List<Node<ComponentDescriptor>> nodeList, Component parentId,
                                   Component newComponent, Component siblingPosition) {

        for (Node<ComponentDescriptor> node : nodeList) {
            Component component = node.getData().getOwnComponent();
            if (component == parentId) {
                if (component instanceof AbstractLayout) {
                    List<Node<ComponentDescriptor>> childList = node.getChildren();
                    ComponentType componentType = DashboardUtils.getTypeByComponent(newComponent);

                    int position = 0;
                    if (childList.size() > 0) {
                        if (siblingPosition == null) {
                            siblingPosition = childList.get(childList.size() - 1).getData().getOwnComponent();
                        }

                        position = getPosition(childList, siblingPosition);
                    }

                    Node<ComponentDescriptor> newNode = new Node<>(new ComponentDescriptor(newComponent, componentType));
                    if (component instanceof DDGridLayout) {
                        int[] coordinates = findGridPosition((GridLayout) component);
                        if (coordinates[0] >= 0 && coordinates[1] >= 0) {
                            newNode.getData().setColumn(coordinates[0]);
                            newNode.getData().setRow(coordinates[1]);
                        } else {
                            return;
                        }
                    }
                    childList.add(position, newNode);
                    node.setChildren(childList);
                    return;
                }
            }

            if (node.getChildren().size() > 0) {
                insertComponent(node.getChildren(), parentId, newComponent, siblingPosition);
            }
        }
    }

    protected int[] findGridPosition(GridLayout gridLayout) {
        int[] coordinates = {-1, -1};
        for (int i = 0; i < gridLayout.getRows(); i++) {
            for (int j = 0; j < gridLayout.getRows(); j++) {
                if (gridLayout.getComponent(i, j) == null) {
                    coordinates[0] = i;
                    coordinates[1] = j;
                    return coordinates;
                }
            }
        }
        return coordinates;
    }

    protected int getPosition(List<Node<ComponentDescriptor>> nodeList, Component siblingPosition) {
        int i = 0;
        for (Node<ComponentDescriptor> node : nodeList) {
            i++;
            if (node.getData().getOwnComponent() == siblingPosition) {
                return i;
            }
        }
        return nodeList.size();
    }

    @Override
    public AcceptCriterion getAcceptCriterion() {
        return AcceptAll.get();
    }

    public void setComponentDescriptorTree(Tree<ComponentDescriptor> componentDescriptorTree) {
        this.componentDescriptorTree = componentDescriptorTree;
    }

    public void setStructureChangeListener(StructureChangeListener structureChangeListener) {
        this.structureChangeListener = structureChangeListener;
    }

    public void setGridDropListener(GridDropListener gridDropListener) {
        this.gridDropListener = gridDropListener;
    }
}
