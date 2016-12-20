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
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.ServerSideCriterion;
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

        if (event.getTransferable() instanceof LayoutBoundTransferable) {
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
                    gridLayout.setColumns(2);
                    gridLayout.setRows(2);
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
                    insertComponent(newComponent, target.getItemIdOver(), null, location);
                } else if (location == VerticalDropLocation.BOTTOM) {
                    Object parentId = tree.getParent(target.getItemIdOver());
                    insertComponent(newComponent, parentId, target.getItemIdOver(), location);
                } else {
                    Object parentId = tree.getParent(target.getItemIdOver());
                    insertComponent(newComponent, parentId, target.getItemIdOver(), location);
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
        } else {
            Transferable t = event.getTransferable();

            if (t.getSourceComponent() != tree) {
                return;
            }

            Object sourceItemId = t.getData("itemId");
            Object targetItemId = target.getItemIdOver();

            HierarchicalContainer container = (HierarchicalContainer)
                    tree.getContainerDataSource();

            if (location == VerticalDropLocation.MIDDLE) {
                tree.setParent(sourceItemId, targetItemId);
                moveComponent(sourceItemId, targetItemId, null, location);
            } else if (location == VerticalDropLocation.TOP) {
                Object parentId = container.getParent(targetItemId);
                container.setParent(sourceItemId, parentId);
                container.moveAfterSibling(sourceItemId, targetItemId);
                container.moveAfterSibling(targetItemId, sourceItemId);
                moveComponent(sourceItemId, parentId, targetItemId, location);
            } else if (location == VerticalDropLocation.BOTTOM) {
                Object parentId = container.getParent(targetItemId);
                container.setParent(sourceItemId, parentId);
                container.moveAfterSibling(sourceItemId, targetItemId);
                container.getItemIds().indexOf(sourceItemId);
                container.getChildren(parentId);
                moveComponent(sourceItemId, parentId, targetItemId, location);
            }
            structureChangeListener.structureChanged(componentDescriptorTree, DropTarget.TREE);
        }
    }

    protected void moveComponent(Object sourceItemId, Object parentId, Object siblingId, VerticalDropLocation location) {
        Node<ComponentDescriptor> oldNode = searchNode(componentDescriptorTree.getRootNodes(), sourceItemId);
        Node<ComponentDescriptor> newParentNode = searchNode(componentDescriptorTree.getRootNodes(), parentId);
        Node<ComponentDescriptor> siblingNode;

        int pos = 0;
        if (siblingId != null) {
            siblingNode = searchNode(componentDescriptorTree.getRootNodes(), siblingId);

            if (siblingNode != null) {
                pos = siblingNode.getParent().getChildren().indexOf(siblingNode);
            }
        }

        oldNode.getParent().getChildren().remove(oldNode);
        newParentNode.getChildren().add(pos, oldNode);
        oldNode.parent = newParentNode;
    }

    protected Node<ComponentDescriptor> searchNode(List<Node<ComponentDescriptor>> nodeList, Object compId) {
        for (Node<ComponentDescriptor> node : nodeList) {
            if (node.getData().getOwnComponent() == compId) {
                return node;
            }
            if (node.getChildren().size() > 0) {
                Node<ComponentDescriptor> childNode = searchNode(node.getChildren(), compId);
                if (childNode != null) {
                    return childNode;
                }
            }
        }
        return null;
    }

    protected void insertComponent(Component newComponent, Object parentId, Object siblingId, VerticalDropLocation location) {
        Node<ComponentDescriptor> newNode = new Node<>(new ComponentDescriptor(newComponent, DashboardUtils.getTypeByComponent(newComponent)));
        Node<ComponentDescriptor> newParentNode = searchNode(componentDescriptorTree.getRootNodes(), parentId);
        Node<ComponentDescriptor> siblingNode;

        int pos = 0;
        if (siblingId != null) {
            siblingNode = searchNode(componentDescriptorTree.getRootNodes(), siblingId);

            if (siblingNode != null) {
                if (siblingNode.getParent() != null) {
                    pos = siblingNode.getParent().getChildren().indexOf(siblingNode);
                } else {
                    pos = componentDescriptorTree.getRootNodes().indexOf(siblingNode);
                }
                if (location == VerticalDropLocation.BOTTOM) {
                    pos++;
                }
            }
        }

        if (newParentNode.getData().getOwnComponent() instanceof GridLayout) {
            int[] coor = findGridPosition((GridLayout) newParentNode.getData().getOwnComponent());
            if (coor[0] == -1 || coor[1] == -1) {
                return;
            }
            newNode.getData().setRow(coor[0]);
            newNode.getData().setColumn(coor[1]);
        }

        if (parentId != null) {
            newParentNode.getChildren().add(pos, newNode);
            newNode.parent = newParentNode;
        } else {
            componentDescriptorTree.getRootNodes().add(pos, newNode);
        }
    }

    protected int[] findGridPosition(GridLayout gridLayout) {
        int[] coordinates = {-1, -1};
        for (int i = 0; i < gridLayout.getRows(); i++) {
            for (int j = 0; j < gridLayout.getColumns(); j++) {
                if (gridLayout.getComponent(j, i) != null
                        && gridLayout.getComponent(j, i) instanceof Label) {
                    coordinates[0] = i;
                    coordinates[1] = j;
                    return coordinates;
                }
            }
        }
        return coordinates;
    }

    @Override
    public AcceptCriterion getAcceptCriterion() {
        ServerSideCriterion serverSideCriterion = new ServerSideCriterion() {
            @Override
            public boolean accept(DragAndDropEvent dragEvent) {
                com.vaadin.ui.Tree.TreeTargetDetails targetDetails =
                        (com.vaadin.ui.Tree.TreeTargetDetails) dragEvent.getTargetDetails();
                VerticalDropLocation location = targetDetails.getDropLocation();
                if (targetDetails.getItemIdOver().toString().contains("WidgetPanel")) {
                    if (location == VerticalDropLocation.MIDDLE) {
                        return false;
                    } else {
                        return true;
                    }
                }
                if (targetDetails.getItemIdInto() != null
                        && targetDetails.getItemIdInto().toString().contains("Grid")) {
                    if (location != VerticalDropLocation.MIDDLE) {
                        return false;
                    } else {
                        return true;
                    }
                }
                if (targetDetails.getTarget().getParent(targetDetails.getItemIdInto()) == null
                        && location != VerticalDropLocation.MIDDLE) {
                    return false;
                }
                return true;
            }
        };
        return serverSideCriterion;
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
