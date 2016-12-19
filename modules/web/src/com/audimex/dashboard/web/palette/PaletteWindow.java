/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.palette;

import com.audimex.dashboard.entity.ComponentType;
import com.audimex.dashboard.entity.DropTarget;
import com.audimex.dashboard.web.ComponentDescriptor;
import com.audimex.dashboard.web.drophandlers.DDVerticalLayoutDropHandler;
import com.audimex.dashboard.web.drophandlers.TreeDropHandler;
import com.haulmont.bali.datastruct.Node;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.CheckBox;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;
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

    @Inject
    private CheckBox allowEdit;

    @Inject
    private com.haulmont.cuba.gui.components.Button removeComponent;

    protected Tree tree;
    protected com.haulmont.bali.datastruct.Tree<ComponentDescriptor> componentStructureTree =
            new com.haulmont.bali.datastruct.Tree<>();

    protected DDVerticalLayout rootDashboardPanel = new DDVerticalLayout();

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        Layout containersLayout = (Layout) WebComponentsHelper.unwrap(containers);
        Layout dropLayoutContainer = (Layout) WebComponentsHelper.unwrap(dropLayout);
        Layout treeLayoutContainer = (Layout) WebComponentsHelper.unwrap(treeLayout);

        DDVerticalLayout containersDraggableLayout = new DDVerticalLayout();
        containersDraggableLayout.setDragMode(LayoutDragMode.CLONE_OTHER);
        containersDraggableLayout.setDragImageProvider(component -> {
            Label label = new Label("");
            label.setIcon(FontAwesome.ASTERISK);
            return label;
        });
        containersDraggableLayout.setSpacing(true);

        List<Node<ComponentDescriptor>> childNodes = new ArrayList<>();
        childNodes.add(new Node<>(new ComponentDescriptor(rootDashboardPanel, ComponentType.VERTICAL_LAYOUT)));
        componentStructureTree.setRootNodes(childNodes);
        rootDashboardPanel.setDragMode(LayoutDragMode.CLONE);

        tree = new Tree();
        tree.setSizeFull();

        TreeDropHandler treeDropHandler = new TreeDropHandler();
        treeDropHandler.setStructureChangeListener((structure, dropTarget) -> {
            onStructureChanged(dropTarget);
        });
        treeDropHandler.setComponentDescriptorTree(componentStructureTree);
        treeDropHandler.setGridDropListener(this::onGridDrop);

        tree.setDropHandler(treeDropHandler);
        tree.addValueChangeListener(e -> {
            if (tree.getValue() == null) {
                removeComponent.setEnabled(false);
            } else {
                removeComponent.setEnabled(true);
            }
        });

        rootDashboardPanel.setSpacing(true);
        rootDashboardPanel.setMargin(true);
        DDVerticalLayoutDropHandler ddVerticalLayoutDropHandler = new DDVerticalLayoutDropHandler();
        ddVerticalLayoutDropHandler.setStructureChangeListener((structure, dropTarget) -> {
            onStructureChanged(dropTarget);
        });
        ddVerticalLayoutDropHandler.setGridDropListener(this::onGridDrop);
        ddVerticalLayoutDropHandler.setComponentDescriptorTree(componentStructureTree);
        rootDashboardPanel.setDropHandler(ddVerticalLayoutDropHandler);

        drawTreeComponent(componentStructureTree.getRootNodes());

        rootDashboardPanel.setSizeFull();
        rootDashboardPanel.setStyleName("dd-bordering");

        setupWidgetsPalette(containersDraggableLayout);

        dropLayoutContainer.addComponent(rootDashboardPanel);
        containersLayout.addComponent(containersDraggableLayout);
        treeLayoutContainer.addComponent(tree);

        allowEdit.setValue(true);
        allowEdit.addValueChangeListener(e -> {
            if (allowEdit.getValue()) {
                containersDraggableLayout.setDragMode(LayoutDragMode.CLONE);
                removeSpacings(rootDashboardPanel, true);
                containersDraggableLayout.removeStyleName("dd-container-disabled");
                rootDashboardPanel.setDragMode(LayoutDragMode.CLONE);
            } else {
                containersDraggableLayout.setDragMode(LayoutDragMode.NONE);
                removeSpacings(rootDashboardPanel, false);
                containersDraggableLayout.addStyleName("dd-container-disabled");
                rootDashboardPanel.setDragMode(LayoutDragMode.NONE);
            }
        });
    }

    private void onStructureChanged(DropTarget dropTarget) {
        if (dropTarget != DropTarget.LAYOUT) {
            rootDashboardPanel.removeAllComponents();
            removeAllComponentsFromLayout(componentStructureTree.getRootNodes());
            buildLayout(componentStructureTree.getRootNodes());
        }
        if (dropTarget != DropTarget.TREE) {
            tree.removeAllItems();
            drawTreeComponent(componentStructureTree.getRootNodes());
        }
        if (dropTarget != DropTarget.REORDER) {
            rootDashboardPanel.removeAllComponents();
            removeAllComponentsFromLayout(componentStructureTree.getRootNodes());
            buildLayout(componentStructureTree.getRootNodes());
            tree.removeAllItems();
            drawTreeComponent(componentStructureTree.getRootNodes());
        }
    }

    private void onGridDrop(GridLayout gridLayout) {
        Window subWindow = new Window("Grid settings");
        subWindow.setModal(true);
        VerticalLayout subContent = new VerticalLayout();
        subContent.setSpacing(true);
        subWindow.setContent(subContent);

        HorizontalLayout buttonsPanel = new HorizontalLayout();
        HorizontalLayout comboBoxPanel = new HorizontalLayout();
        buttonsPanel.setSpacing(true);
        comboBoxPanel.setSpacing(true);
        ComboBox cols = new ComboBox();
        ComboBox rows = new ComboBox();

        cols.setNullSelectionAllowed(false);
        rows.setNullSelectionAllowed(false);

        for (int i=1; i<=10; i++) {
            cols.addItem(i);
        }

        for (int i=1; i<=10; i++) {
            rows.addItem(i);
        }

        cols.setValue(2);
        rows.setValue(2);
        cols.setCaption("Columns");
        rows.setCaption("Rows");
        cols.focus();

        comboBoxPanel.addComponent(cols);
        comboBoxPanel.addComponent(rows);
        subContent.addComponent(comboBoxPanel);
        subContent.addComponent(buttonsPanel);

        Button closeButton = new Button("Close", FontAwesome.CLOSE);
        Button okButton = new Button("Ok", FontAwesome.CHECK);
        closeButton.addClickListener(event -> subWindow.close());

        okButton.addClickListener(event -> {
            if (cols.getValue() != null && rows.getValue() != null) {
                gridLayout.setColumns(Integer.parseInt(cols.getValue().toString()));
                gridLayout.setRows(Integer.parseInt(rows.getValue().toString()));
                for (int i=0; i<gridLayout.getRows(); i++) {
                    for (int j=0; j<gridLayout.getColumns(); j++) {
                        Label label = new Label("");
                        label.setSizeFull();
                        label.addStyleName("dd-grid-slot");
                        gridLayout.addComponent(label);
                        gridLayout.setComponentAlignment(label, com.vaadin.ui.Alignment.MIDDLE_CENTER);
                    }
                }
                subWindow.close();
            }
        });

        buttonsPanel.addComponent(okButton);
        buttonsPanel.addComponent(closeButton);
        buttonsPanel.setComponentAlignment(closeButton, com.vaadin.ui.Alignment.MIDDLE_RIGHT);
        buttonsPanel.setComponentAlignment(okButton, com.vaadin.ui.Alignment.MIDDLE_RIGHT);

        subWindow.center();
        UI.getCurrent().addWindow(subWindow);
    }

    private void setupWidgetsPalette(DDVerticalLayout containersDraggableLayout) {
        Button verticalLayoutButton = new Button("Vertical", FontAwesome.ARROWS_V);
        verticalLayoutButton.setId("verticalLayout");
        verticalLayoutButton.setWidth("100%");
        verticalLayoutButton.setHeight("50px");
        verticalLayoutButton.setStyleName("dd-palette-button");

        Button horizontalLayoutButton = new Button("Horizontal", FontAwesome.ARROWS_H);
        horizontalLayoutButton.setId("horizontalLayout");
        horizontalLayoutButton.setWidth("100%");
        horizontalLayoutButton.setHeight("50px");
        horizontalLayoutButton.setStyleName("dd-palette-button");

        Button gridButton = new Button("Grid", FontAwesome.TH);
        gridButton.setId("gridLayout");
        gridButton.setWidth("100%");
        gridButton.setHeight("50px");
        gridButton.setStyleName("dd-palette-button");

        Button widgetButton = new Button("Widget", FontAwesome.BAR_CHART);
        widgetButton.setId("widgetPanel");
        widgetButton.setWidth("100%");
        widgetButton.setHeight("50px");
        widgetButton.setStyleName("dd-palette-button");

        containersDraggableLayout.addComponent(verticalLayoutButton);
        containersDraggableLayout.addComponent(horizontalLayoutButton);
        containersDraggableLayout.addComponent(gridButton);
        containersDraggableLayout.addComponent(widgetButton);
    }

    private void removeAllComponentsFromLayout(List<Node<ComponentDescriptor>> nodeList) {
        for (Node<ComponentDescriptor> node : nodeList) {
            Component component = node.getData().getOwnComponent();
            if (component instanceof GridLayout) {
                ((GridLayout) component).removeAllComponents();
            } else if (component instanceof AbstractOrderedLayout) {
                ((AbstractOrderedLayout) component).removeAllComponents();
            }

            if (node.getChildren().size() > 0) {
                removeAllComponentsFromLayout(node.getChildren());
            }
        }
    }

    private void removeSpacings(AbstractLayout component, boolean value) {
        if (value) {
            component.addStyleName("dd-bordering");
        } else {
            component.removeStyleName("dd-bordering");
        }

        if (component instanceof AbstractOrderedLayout) {
            ((AbstractOrderedLayout) component).setMargin(value);
        } else if (component instanceof GridLayout) {
            ((GridLayout) component).setMargin(value);
        }

        for (int i=0; i<component.getComponentCount(); i++) {
            if (component instanceof AbstractOrderedLayout) {
                Component child = ((AbstractOrderedLayout) component).getComponent(i);
                removeSpacings((AbstractLayout) child, value);
            } else if (component instanceof GridLayout) {
                GridLayout grid = (GridLayout) component;
                for (int k=0; k < grid.getRows(); k++) {
                    for (int j=0; j < grid.getColumns(); j++) {
                        if (grid.getComponent(j, k) instanceof AbstractLayout) {
                            removeSpacings((AbstractLayout) grid.getComponent(j, k), value);
                        } else if (grid.getComponent(j, k) instanceof Label) {
                            grid.getComponent(j, k).setVisible(value);
                        }
                    }
                }
            }
        }
    }

    private void buildStructure() {
        List<Node<ComponentDescriptor>> nodeList = getChildNodesFromLayout(rootDashboardPanel);
        componentStructureTree.setRootNodes(nodeList);
    }

    private void buildLayout(List<Node<ComponentDescriptor>> nodeList) {
        buildLayoutFromTree(nodeList.get(0).getChildren(), rootDashboardPanel);
    }

    private void buildLayoutFromTree(List<Node<ComponentDescriptor>> nodeList, AbstractLayout container) {
        for (Node<ComponentDescriptor> node : nodeList) {
            if (container instanceof GridLayout) {
                Component existingComponent = ((GridLayout) container).getComponent(node.getData().getColumn(), node.getData().getRow());
                if (existingComponent != null) {
                    if (existingComponent instanceof Label) {
                        container.removeComponent(existingComponent);
                    }
                }
                ((GridLayout) container).addComponent(node.getData().getOwnComponent(), node.getData().getColumn(), node.getData().getRow());
            } else {
                container.addComponent(node.getData().getOwnComponent());
            }
            if (node.getChildren().size() > 0 && node.getData().getComponentType() != ComponentType.WIDGET) {
                buildLayoutFromTree(node.getChildren(), (AbstractLayout) node.getData().getOwnComponent());

                if (node.getData().getOwnComponent() instanceof GridLayout) {
                    fillGrid((GridLayout) node.getData().getOwnComponent());
                }
            }
        }
    }

    private void fillGrid(GridLayout gridLayout) {
        for (int i=0; i<gridLayout.getRows(); i++) {
            for (int j=0; j<gridLayout.getColumns(); j++) {
                if (gridLayout.getComponent(j, i) == null) {
                    Label label = new Label("");
                    label.addStyleName("dd-grid-slot");
                    label.setSizeFull();
                    gridLayout.addComponent(label, j, i);
                    gridLayout.setComponentAlignment(label, com.vaadin.ui.Alignment.MIDDLE_CENTER);
                }
            }
        }
    }

    private List<Node<ComponentDescriptor>> getChildNodesFromLayout(AbstractOrderedLayout rootComponent) {
        List<Node<ComponentDescriptor>> nodeList = new ArrayList<>();

        for (int i = 0; i < rootComponent.getComponentCount(); i++) {
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
            Node<ComponentDescriptor> node = new Node<>(componentDescriptor);

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
        for (Node<ComponentDescriptor> node : nodeList) {
            Component component = node.getData().getOwnComponent();
            ComponentDescriptor componentDescriptor = node.getData();
            ComponentDescriptor parent;

            tree.addItem(component.toString());

            if (node.getParent() != null) {
                parent = node.getParent().getData();
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
                case GRID_LAYOUT:
                    tree.setItemCaption(component.toString(), "Grid");
                    break;
                case WIDGET:
                    tree.setItemCaption(component.toString(), "Widget");
                    break;
            }

            if (component instanceof AbstractLayout) {
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

    public void removeComponent() {
        if (tree.getParent(tree.getValue()) != null) {
            findAndRemoveComponent(componentStructureTree.getRootNodes(), tree.getValue().toString());

            onStructureChanged(DropTarget.REORDER);
        } else {
            showNotification("You can't remove the root component", NotificationType.HUMANIZED);
        }
    }

    private void findAndRemoveComponent(List<Node<ComponentDescriptor>> nodeList, String componentId) {
        for (Node<ComponentDescriptor> node : nodeList) {
            if (node.getData().getOwnComponent().toString().equals(componentId)) {
                nodeList.remove(nodeList.indexOf(node));
                return;
            }

            if (node.getChildren().size() > 0) {
                findAndRemoveComponent(node.getChildren(), componentId);
            }
        }
    }
}