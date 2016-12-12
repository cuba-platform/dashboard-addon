/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.palette;

import com.audimex.dashboard.entity.ComponentType;
import com.audimex.dashboard.web.ComponentDescriptor;
import com.audimex.dashboard.web.drophandlers.DDVerticalLayoutDropHandler;
import com.haulmont.bali.datastruct.Node;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.vaadin.ui.*;
import fi.jasoft.dragdroplayouts.DDVerticalLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PaletteWindow extends AbstractWindow {

    @Inject
    private VBoxLayout containers;

    @Inject
    private VBoxLayout dropLayout;

    @Inject
    private VBoxLayout treeLayout;

    protected Tree tree;
    protected com.haulmont.bali.datastruct.Tree<ComponentDescriptor> componentStructureTree = new com.haulmont.bali.datastruct.Tree<>();
    protected DDVerticalLayout dropDraggableLayout = new DDVerticalLayout();

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        Layout containersLayout = (Layout) WebComponentsHelper.unwrap(containers);
        Layout dropLayoutContainer = (Layout) WebComponentsHelper.unwrap(dropLayout);
        Layout treeLayoutContainer = (Layout) WebComponentsHelper.unwrap(treeLayout);

        DDVerticalLayout containersDraggableLayout = new DDVerticalLayout();
        containersDraggableLayout.setDragMode(LayoutDragMode.CLONE);
        containersDraggableLayout.setSpacing(true);

        dropDraggableLayout.setDragMode(LayoutDragMode.CLONE);

        tree = new Tree();
        tree.setSizeFull();

        dropDraggableLayout.setSpacing(true);
        dropDraggableLayout.setMargin(true);
        dropDraggableLayout.setDropHandler(new DDVerticalLayoutDropHandler());

        buildStructure();
        drawTreeComponent(componentStructureTree.getRootNodes());

        ((DDVerticalLayoutDropHandler) dropDraggableLayout.getDropHandler()).addStructureChangeListener(() -> {
            tree.removeAllItems();

            buildStructure();
            drawTreeComponent(componentStructureTree.getRootNodes());
        });
        dropDraggableLayout.setSizeFull();
        dropDraggableLayout.setStyleName("dd-bordering");

        Button verticalLayoutButton = new Button("Vertical");
        verticalLayoutButton.setId("verticalLayout");
        verticalLayoutButton.setWidth("100%");
        verticalLayoutButton.setHeight("50px");
        Button horizontalLayoutButton = new Button("Horizontal");
        horizontalLayoutButton.setId("horizontalLayout");
        horizontalLayoutButton.setWidth("100%");
        horizontalLayoutButton.setHeight("50px");
        Button widgetButton = new Button("Widget");
        widgetButton.setId("widgetPanel");
        widgetButton.setWidth("100%");
        widgetButton.setHeight("50px");

        containersDraggableLayout.addComponent(verticalLayoutButton);
        containersDraggableLayout.addComponent(horizontalLayoutButton);
        containersDraggableLayout.addComponent(widgetButton);

        dropLayoutContainer.addComponent(dropDraggableLayout);
        containersLayout.addComponent(containersDraggableLayout);
        treeLayoutContainer.addComponent(tree);
    }

    private void buildStructure() {
        List<Node<ComponentDescriptor>> nodeList = getChildNodesFromLayout(dropDraggableLayout);
        componentStructureTree.setRootNodes(nodeList);
    }

    private List<Node<ComponentDescriptor>> getChildNodesFromLayout(AbstractOrderedLayout rootComponent) {
        List<Node<ComponentDescriptor>> nodeList = new ArrayList<>();

        for (int i=0; i<rootComponent.getComponentCount(); i++) {
            Component component = rootComponent.getComponent(i);
            ComponentType componentType;

            if (component instanceof AbstractOrderedLayout) {
                if (component instanceof DDVerticalLayout) {
                    componentType = ComponentType.VERTICAL_LAYOUT;
                } else {
                    componentType = ComponentType.HORIZONTAL_LAYOUT;
                }
            } else {
                componentType = ComponentType.WIDGET;
            }

            ComponentDescriptor componentDescriptor = new ComponentDescriptor(component, componentType);
            List<Node<ComponentDescriptor>> childNodeList = new ArrayList<>();
            Node node = new Node(componentDescriptor);

            if (component instanceof AbstractOrderedLayout) {
                if (((AbstractOrderedLayout) component).getComponentCount() > 0) {
                    childNodeList = getChildNodesFromLayout((AbstractOrderedLayout) component);
                    node.setChildren(childNodeList);
                }
            }

            node.setData(componentDescriptor);
            nodeList.add(node);
        }

        return nodeList;
    }

    private void drawTreeComponent(List<Node<ComponentDescriptor>> nodeList) {
        for (Node node : nodeList) {
            Component component = ((ComponentDescriptor) node.getData()).getOwnComponent();
            ComponentDescriptor componentDescriptor = (ComponentDescriptor) node.getData();
            ComponentDescriptor parent = null;

            tree.addItem(component.toString());

            if (node.getParent() != null) {
                parent = (ComponentDescriptor) node.getParent().getData();
                tree.setParent(component.toString(), parent.getOwnComponent().toString());
            }
            tree.expandItem(component.toString());

            switch (componentDescriptor.getComponentType()) {
                case VERTICAL_LAYOUT:
                    tree.setItemCaption(component.toString(), "Vertical");
                    break;
                case HORIZONTAL_LAYOUT:
                    tree.setItemCaption(component.toString(), "Horizontal");
                    break;
                case WIDGET:
                    tree.setItemCaption(component.toString(), "Widget");
                    break;
            }

            if (component instanceof AbstractOrderedLayout) {
                int count = node.getChildren().size();
                tree.setChildrenAllowed(component.toString(), count != 0);

                if (count != 0) {
                    drawTreeComponent(node.getChildren());
                }
            } else {
                tree.setChildrenAllowed(component.toString(), false);
            }
        }
    }
}