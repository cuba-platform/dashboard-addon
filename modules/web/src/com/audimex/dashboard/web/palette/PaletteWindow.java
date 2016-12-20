/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.palette;

import com.audimex.dashboard.entity.ComponentType;
import com.audimex.dashboard.entity.DropTarget;
import com.audimex.dashboard.web.ComponentDescriptor;
import com.audimex.dashboard.web.drophandlers.DDVerticalLayoutDropHandler;
import com.audimex.dashboard.web.drophandlers.TreeDropHandler;
import com.audimex.dashboard.web.utils.DashboardUtils;
import com.haulmont.bali.datastruct.Node;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.CheckBox;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.security.app.UserSettingService;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import fi.jasoft.dragdroplayouts.DDVerticalLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PaletteWindow extends AbstractWindow {

    @Inject
    private VBoxLayout containers;

    @Inject
    private UserSettingService userSettingService;

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
        containersDraggableLayout.setDragMode(DashboardUtils.getDefaultDragMode());
        containersDraggableLayout.setSpacing(true);

        List<Node<ComponentDescriptor>> childNodes = new ArrayList<>();
        childNodes.add(new Node<>(new ComponentDescriptor(rootDashboardPanel, ComponentType.VERTICAL_LAYOUT)));
        componentStructureTree.setRootNodes(childNodes);
        rootDashboardPanel.setDragMode(DashboardUtils.getDefaultDragMode());

        tree = new Tree();
        tree.setSizeFull();

        TreeDropHandler treeDropHandler = new TreeDropHandler(dropLayout);
        treeDropHandler.setStructureChangeListener((structure, dropTarget) ->
                onStructureChanged(dropTarget)
        );
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
        tree.setDragMode(Tree.TreeDragMode.NODE);

        rootDashboardPanel.setSpacing(true);
        rootDashboardPanel.setMargin(true);
        DDVerticalLayoutDropHandler ddVerticalLayoutDropHandler = new DDVerticalLayoutDropHandler(dropLayout);
        ddVerticalLayoutDropHandler.setStructureChangeListener((structure, dropTarget) ->
                onStructureChanged(dropTarget)
        );
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
                containersDraggableLayout.setDragMode(DashboardUtils.getDefaultDragMode());
                removeSpacings(rootDashboardPanel, true);
                rootDashboardPanel.setDragMode(DashboardUtils.getDefaultDragMode());

                containersDraggableLayout.removeStyleName("dd-container-disabled");
                rootDashboardPanel.removeStyleName("ad-dashboard-disabled");
            } else {
                containersDraggableLayout.setDragMode(LayoutDragMode.NONE);
                removeSpacings(rootDashboardPanel, false);
                rootDashboardPanel.setDragMode(LayoutDragMode.NONE);

                containersDraggableLayout.addStyleName("dd-container-disabled");
                rootDashboardPanel.addStyleName("ad-dashboard-disabled");
            }

            removeComponent.setEnabled(allowEdit.getValue());
        });
    }

    private void onStructureChanged(DropTarget dropTarget) {
        if (dropTarget == DropTarget.TREE || dropTarget == DropTarget.REORDER) {
            rootDashboardPanel.removeAllComponents();
            removeAllComponentsFromLayout(componentStructureTree.getRootNodes());
            buildLayout(componentStructureTree.getRootNodes());
            tree.removeAllItems();
            drawTreeComponent(componentStructureTree.getRootNodes());
        }
        if (dropTarget == DropTarget.LAYOUT) {
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
                gridLayout.setColumns((Integer) cols.getValue());
                gridLayout.setRows((Integer) rows.getValue());
                DashboardUtils.addEmptyLabels(gridLayout);
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
            ComponentDescriptor cd = node.getData();

            if (container instanceof GridLayout) {
                Component existingComponent = ((GridLayout) container).getComponent(cd.getColumn(), cd.getRow());
                if (existingComponent != null) {
                    if (existingComponent instanceof Label) {
                        container.removeComponent(existingComponent);
                    }
                }

                ((GridLayout) container).addComponent(cd.getOwnComponent(), cd.getColumn(), cd.getRow(),
                        cd.getColumn() + cd.getColSpan() - 1,
                        cd.getRow() + cd.getRowSpan() - 1);
            } else {
                container.addComponent(cd.getOwnComponent());
                ((AbstractOrderedLayout) container).setExpandRatio(cd.getOwnComponent(), cd.getWeight());
            }

            if (cd.getOwnComponent() instanceof GridLayout) {
                fillGrid((GridLayout) cd.getOwnComponent());
            }

            if (node.getChildren().size() > 0 && cd.getComponentType() != ComponentType.WIDGET) {
                buildLayoutFromTree(node.getChildren(), (AbstractLayout) cd.getOwnComponent());

                if (cd.getOwnComponent() instanceof GridLayout) {
                    fillGrid((GridLayout) cd.getOwnComponent());
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
            Node<ComponentDescriptor> node = new Node<>(componentDescriptor);

            if (component instanceof AbstractOrderedLayout) {
                if (((AbstractOrderedLayout) component).getComponentCount() > 0) {
                    List<Node<ComponentDescriptor>> childNodeList = getChildNodesFromLayout((AbstractOrderedLayout) component);
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

            tree.addItem(component);

            if (node.getParent() != null) {
                parent = node.getParent().getData();
                tree.setParent(component, parent.getOwnComponent());
            }
            tree.expandItem(component);

            switch (componentDescriptor.getComponentType()) {
                case VERTICAL_LAYOUT:
                    tree.setItemCaption(component, "Vertical");
                    break;
                case HORIZONTAL_LAYOUT:
                    tree.setItemCaption(component, "Horizontal");
                    break;
                case GRID_LAYOUT:
                    tree.setItemCaption(component, "Grid");
                    break;
                case WIDGET:
                    tree.setItemCaption(component, "Widget");
                    break;
            }

            if (component instanceof AbstractLayout) {
                int count = node.getChildren().size();
                tree.setChildrenAllowed(component, count != 0);

                if (count != 0) {
                    drawTreeComponent(node.getChildren());
                }
            } else {
                tree.setChildrenAllowed(component, false);
            }
        }
    }

    public void removeComponent() {
        if (tree.getParent(tree.getValue()) != null) {
            findAndRemoveComponent(componentStructureTree.getRootNodes(), (Component) tree.getValue());

            onStructureChanged(DropTarget.REORDER);
        } else {
            showNotification("You can't remove the root component", NotificationType.HUMANIZED);
        }
    }

    private void findAndRemoveComponent(List<Node<ComponentDescriptor>> nodeList, Component component) {
        for (Node<ComponentDescriptor> node : nodeList) {
            if (node.getData().getOwnComponent() == component) {
                nodeList.remove(nodeList.indexOf(node));
                return;
            }

            if (node.getChildren().size() > 0) {
                findAndRemoveComponent(node.getChildren(), component);
            }
        }
    }

    private String convertToJson(List<Node<ComponentDescriptor>> nodeList) {
        JSONArray ja = new JSONArray();
        for (Node<ComponentDescriptor> node : nodeList) {
            JSONObject jo = new JSONObject();
            jo.put("component", "component");
            if (node.getChildren().size() > 0) {
                jo.put("children", convertToJson(node.getChildren()));
            }
            ja.put(jo);
        }
        return ja.toString();
    }

    @Override
    public void saveSettings() {
        String json = convertToJson(componentStructureTree.getRootNodes());
        userSettingService.saveSetting("componentStructureTree", json);

        super.saveSettings();
    }
}