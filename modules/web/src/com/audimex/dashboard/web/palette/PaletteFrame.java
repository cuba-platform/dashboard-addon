/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.palette;

import com.audimex.dashboard.entity.ComponentType;
import com.audimex.dashboard.entity.DemoContentType;
import com.audimex.dashboard.entity.DropTarget;
import com.audimex.dashboard.web.ComponentDescriptor;
import com.audimex.dashboard.web.drophandlers.DDGridLayoutDropHandler;
import com.audimex.dashboard.web.drophandlers.DDHorizontalLayoutDropHandler;
import com.audimex.dashboard.web.drophandlers.DDVerticalLayoutDropHandler;
import com.audimex.dashboard.web.drophandlers.TreeDropHandler;
import com.audimex.dashboard.web.utils.DashboardUtils;
import com.audimex.dashboard.web.widgets.WidgetPanel;
import com.haulmont.bali.datastruct.Node;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.CheckBox;
import com.haulmont.cuba.security.app.UserSettingService;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;
import fi.jasoft.dragdroplayouts.DDGridLayout;
import fi.jasoft.dragdroplayouts.DDHorizontalLayout;
import fi.jasoft.dragdroplayouts.DDVerticalLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PaletteFrame extends AbstractFrame {

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
    private VBoxLayout palette;

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
                palette.setVisible(true);
                containersDraggableLayout.removeStyleName("dd-container-disabled");
                rootDashboardPanel.removeStyleName("ad-dashboard-disabled");
            } else {
                containersDraggableLayout.setDragMode(LayoutDragMode.NONE);
                removeSpacings(rootDashboardPanel, false);
                rootDashboardPanel.setDragMode(LayoutDragMode.NONE);
                palette.setVisible(false);
                containersDraggableLayout.addStyleName("dd-container-disabled");
                rootDashboardPanel.addStyleName("ad-dashboard-disabled");
            }

            removeComponent.setEnabled(allowEdit.getValue());
        });

        loadSettings();
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

                DashboardUtils.removeEmptyLabels(((GridLayout) container));
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

    private JSONArray convertToJson(List<Node<ComponentDescriptor>> nodeList) {
        JSONArray ja = new JSONArray();
        for (Node<ComponentDescriptor> node : nodeList) {
            JSONObject jo = new JSONObject();
            jo.put("component", convertComponentToJSON(node.getData()));
            if (node.getChildren().size() > 0) {
                jo.put("children", convertToJson(node.getChildren()));
            }
            ja.put(jo);
        }
        return ja;
    }

    private JSONObject convertComponentToJSON(ComponentDescriptor componentDescriptor) {
        JSONObject jo = new JSONObject();
        switch (componentDescriptor.getComponentType()) {
            case VERTICAL_LAYOUT:
                jo.put("component_type", "vertical");
                break;
            case HORIZONTAL_LAYOUT:
                jo.put("component_type", "horizontal");
                break;
            case WIDGET:
                jo.put("component_type", "widget");
                break;
            case GRID_LAYOUT:
                jo.put("component_type", "grid");
                break;
        }

        jo.put("column", componentDescriptor.getColumn());
        jo.put("row", componentDescriptor.getRow());
        jo.put("caption", componentDescriptor.getCaption());
        jo.put("colspan", componentDescriptor.getColSpan());
        jo.put("rowspan", componentDescriptor.getRowSpan());
        jo.put("icon", componentDescriptor.getIcon());
        jo.put("weight", componentDescriptor.getWeight());
        if (componentDescriptor.getOwnComponent() instanceof WidgetPanel
                && componentDescriptor.getDemoContentType() != null) {
            switch (componentDescriptor.getDemoContentType()) {
                case PRODUCTS_TABLE:
                    jo.put("contentType", "table");
                    break;
                case SALES_CHART:
                    jo.put("contentType", "chart");
                    break;
                case INVOICE_REPORT:
                    jo.put("contentType", "report");
                    break;
            }
        }

        if (componentDescriptor.getOwnComponent() instanceof GridLayout) {
            jo.put("columnCount", ((GridLayout) componentDescriptor.getOwnComponent()).getColumns());
            jo.put("rowCount", ((GridLayout) componentDescriptor.getOwnComponent()).getColumns());
        }
        return jo;
    }

    private List<Node<ComponentDescriptor>> convertJsonToTree(JSONArray jsonArray) {
        List<Node<ComponentDescriptor>> nodeList = new ArrayList<>();
        for (int i = 0; i< jsonArray.length(); i++) {
            Node<ComponentDescriptor> node = new Node<>();
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            JSONObject jsonComponent = (JSONObject) jsonObject.get("component");
            ComponentType componentType = null;
            Component component = null;
            switch ((String) jsonComponent.get("component_type")) {
                case "vertical":
                    componentType = ComponentType.VERTICAL_LAYOUT;
                    DDVerticalLayout ddVerticalLayout = new DDVerticalLayout();
                    ddVerticalLayout.setMargin(true);
                    ddVerticalLayout.setSpacing(true);
                    ddVerticalLayout.setSizeFull();
                    ddVerticalLayout.setStyleName("dd-bordering");
                    ddVerticalLayout.setDragMode(LayoutDragMode.CLONE);

                    DDVerticalLayoutDropHandler ddVerticalLayoutDropHandler = new DDVerticalLayoutDropHandler(dropLayout);
                    ddVerticalLayoutDropHandler.setStructureChangeListener((structure, dropTarget) ->
                            onStructureChanged(dropTarget)
                    );
                    ddVerticalLayoutDropHandler.setGridDropListener(this::onGridDrop);
                    ddVerticalLayoutDropHandler.setComponentDescriptorTree(componentStructureTree);
                    ddVerticalLayout.setDropHandler(ddVerticalLayoutDropHandler);

                    component = ddVerticalLayout;
                    break;
                case "horizontal":
                    componentType = ComponentType.HORIZONTAL_LAYOUT;
                    DDHorizontalLayout ddHorizontalLayout = new DDHorizontalLayout();
                    ddHorizontalLayout.setMargin(true);
                    ddHorizontalLayout.setSpacing(true);
                    ddHorizontalLayout.setSizeFull();
                    ddHorizontalLayout.setStyleName("dd-bordering");
                    ddHorizontalLayout.setDragMode(LayoutDragMode.CLONE);

                    DDHorizontalLayoutDropHandler ddHorizontalLayoutDropHandler = new DDHorizontalLayoutDropHandler(dropLayout);
                    ddHorizontalLayoutDropHandler.setStructureChangeListener((structure, dropTarget) ->
                            onStructureChanged(dropTarget)
                    );
                    ddHorizontalLayoutDropHandler.setGridDropListener(this::onGridDrop);
                    ddHorizontalLayoutDropHandler.setComponentDescriptorTree(componentStructureTree);
                    ddHorizontalLayout.setDropHandler(ddHorizontalLayoutDropHandler);

                    component = ddHorizontalLayout;
                    break;
                case "widget":
                    componentType = ComponentType.WIDGET;
                    WidgetPanel widgetPanel = new WidgetPanel(dropLayout);
                    widgetPanel.setHeaderIcon(DashboardUtils.iconNames.inverse().get((String) jsonComponent.get("icon")));
                    widgetPanel.setHeaderText((String) jsonComponent.get("caption"));
                    if (jsonComponent.has("contentType")) {
                        switch ((String) jsonComponent.get("contentType")) {
                            case "table":
                                widgetPanel.setDemoContentType(DemoContentType.PRODUCTS_TABLE);
                                break;
                            case "chart":
                                widgetPanel.setDemoContentType(DemoContentType.SALES_CHART);
                                break;
                            case "report":
                                widgetPanel.setDemoContentType(DemoContentType.INVOICE_REPORT);
                                break;
                            default:
                                break;
                        }
                    }
                    component = widgetPanel;
                    break;
                case "grid":
                    componentType = ComponentType.GRID_LAYOUT;
                    DDGridLayout ddGridLayout = new DDGridLayout();
                    ddGridLayout.setMargin(true);
                    ddGridLayout.setSpacing(true);
                    ddGridLayout.setSizeFull();
                    ddGridLayout.setStyleName("dd-bordering");
                    ddGridLayout.setDragMode(LayoutDragMode.CLONE);
                    ddGridLayout.setColumns((Integer) jsonComponent.get("columnCount"));
                    ddGridLayout.setRows((Integer) jsonComponent.get("rowCount"));

                    DDGridLayoutDropHandler ddGridLayoutDropHandler = new DDGridLayoutDropHandler(dropLayout);
                    ddGridLayoutDropHandler.setComponentDescriptorTree(componentStructureTree);
                    ddGridLayoutDropHandler.setStructureChangeListener((structure, dropTarget) ->
                            onStructureChanged(dropTarget)
                    );
                    ddGridLayoutDropHandler.setGridDropListener(this::onGridDrop);
                    ddGridLayout.setDropHandler(ddGridLayoutDropHandler);

                    component = ddGridLayout;
                    break;
            }
            ComponentDescriptor cd = new ComponentDescriptor(component, componentType);
            cd.setColumn((Integer) jsonComponent.get("column"));
            cd.setRow((Integer) jsonComponent.get("row"));
            cd.setCaption((String) jsonComponent.get("caption"));
            cd.setColSpan((Integer) jsonComponent.get("colspan"));
            cd.setRowSpan((Integer) jsonComponent.get("rowspan"));
            cd.setIcon((String) jsonComponent.get("icon"));
            cd.setWeight((Integer) jsonComponent.get("weight"));
            if (componentType == ComponentType.WIDGET) {
                if (jsonComponent.has("contentType")) {
                    switch ((String) jsonComponent.get("contentType")) {
                        case "table":
                            cd.setDemoContentType(DemoContentType.PRODUCTS_TABLE);
                            break;
                        case "chart":
                            cd.setDemoContentType(DemoContentType.SALES_CHART);
                            break;
                        case "report":
                            cd.setDemoContentType(DemoContentType.INVOICE_REPORT);
                            break;
                    }
                }
            }

            if (jsonObject.has("children")) {
                node.setChildren(convertJsonToTree((JSONArray) jsonObject.get("children")));
            }
            node.setData(cd);
            nodeList.add(node);
        }
        return nodeList;
    }

    private void loadSettings() {
        if (userSettingService.loadSetting("componentStructureTree") != null) {
            JSONArray jsonArray = new JSONArray(userSettingService.loadSetting("componentStructureTree"));
            componentStructureTree.setRootNodes(convertJsonToTree(jsonArray));
            onStructureChanged(DropTarget.REORDER);
        }
    }

    public void clearAll() {
        showOptionDialog("Are you sure?",
                "All components will be removed",
                MessageType.CONFIRMATION,
                new Action[]{
                new DialogAction(DialogAction.Type.YES) {
                    @Override
                    public void actionPerform(com.haulmont.cuba.gui.components.Component component) {
                        List<Node<ComponentDescriptor>> childNodes = new ArrayList<>();
                        childNodes.add(new Node<>(new ComponentDescriptor(rootDashboardPanel, ComponentType.VERTICAL_LAYOUT)));
                        componentStructureTree.setRootNodes(childNodes);
                        onStructureChanged(DropTarget.REORDER);
                        userSettingService.saveSetting("componentStructureTree", null);
                    }
                },
                new DialogAction(DialogAction.Type.NO) {
                    @Override
                    public void actionPerform(com.haulmont.cuba.gui.components.Component component) {
                    }
                }
        });
    }

//    @Override
//    protected boolean preClose(String actionId) {
//        JSONArray json = convertToJson(componentStructureTree.getRootNodes());
//        userSettingService.saveSetting("componentStructureTree", json.toString());
//        return super.preClose(actionId);
//    }
}