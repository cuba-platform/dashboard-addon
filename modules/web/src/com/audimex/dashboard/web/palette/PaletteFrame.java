/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.palette;

import com.audimex.dashboard.entity.ComponentType;
import com.audimex.dashboard.entity.WidgetType;
import com.audimex.dashboard.web.ComponentDescriptor;
import com.audimex.dashboard.web.drophandlers.DDVerticalLayoutDropHandler;
import com.audimex.dashboard.web.drophandlers.TreeDropHandler;
import com.audimex.dashboard.web.repo.WidgetRepository;
import com.audimex.dashboard.web.settings.DashboardSettings;
import com.audimex.dashboard.web.utils.DashboardUtils;
import com.audimex.dashboard.web.utils.TreeUtils;
import com.audimex.dashboard.web.widgets.GridCell;
import com.audimex.dashboard.web.widgets.GridRow;
import com.haulmont.bali.datastruct.Node;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.CheckBox;
import com.haulmont.cuba.security.app.UserSettingService;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.LayoutEvents;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;
import fi.jasoft.dragdroplayouts.DDCssLayout;
import fi.jasoft.dragdroplayouts.DDVerticalLayout;
import fi.jasoft.dragdroplayouts.DragCaption;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;
import fi.jasoft.dragdroplayouts.interfaces.DragGrabFilter;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
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
    private SplitPanel palette;

    @Inject
    private DashboardSettings dashboardSettings;

    @Inject
    private WidgetRepository widgetRepository;

    @Inject
    private com.haulmont.cuba.gui.components.Button removeComponent;

    protected Tree tree;

    protected DDVerticalLayout rootDashboardPanel = new DDVerticalLayout();
    protected Component treeComponent;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        Layout containersLayout = (Layout) WebComponentsHelper.unwrap(containers);
        Layout dropLayoutContainer = (Layout) WebComponentsHelper.unwrap(dropLayout);
        Layout treeLayoutContainer = (Layout) WebComponentsHelper.unwrap(treeLayout);

        DDCssLayout containersDraggableLayout = new DDCssLayout();
        containersDraggableLayout.setDragMode(DashboardUtils.getDefaultDragMode());
        containersDraggableLayout.setDragCaptionProvider(
                component -> new DragCaption(component.getCaption(), component.getIcon())
        );

        List<Node<ComponentDescriptor>> childNodes = new ArrayList<>();
        childNodes.add(new Node<>(new ComponentDescriptor(rootDashboardPanel, ComponentType.VERTICAL_LAYOUT)));
        rootDashboardPanel.setDragMode(DashboardUtils.getDefaultDragMode());
        rootDashboardPanel.setDragGrabFilter((DragGrabFilter) component -> dashboardSettings.isComponentDraggable(component));

        tree = new Tree();
        tree.setSizeFull();

        TreeDropHandler treeDropHandler = new TreeDropHandler();
        treeDropHandler.setComponentDescriptorTree(tree);
        treeDropHandler.setGridDropListener(this::onGridDrop);
        treeDropHandler.setTreeChangeListener(() -> TreeUtils.redrawLayout(tree, rootDashboardPanel));

        tree.setDropHandler(treeDropHandler);
        tree.addValueChangeListener(e -> {
            if (treeComponent != null) {
                treeComponent.removeStyleName("tree-selected");
            }

            if (tree.getValue() == null) {
                removeComponent.setEnabled(false);
            } else {
                removeComponent.setEnabled(true);
                Object treeObject = tree.getValue();
                if (treeObject instanceof Component) {
                    if (treeObject instanceof GridCell) {
                        Collection<?> gridCellChild = tree.getChildren(treeObject);
                        if (gridCellChild != null) {
                            treeComponent = (Component) gridCellChild.iterator().next();
                        } else {
                            treeComponent = ((GridCell) treeObject).getParent();
                        }
                    } else {
                        treeComponent = (Component) treeObject;
                    }
                } else if (treeObject instanceof GridRow) {
                    treeComponent = (Component) tree.getParent(treeObject);
                }

                treeComponent.addStyleName("tree-selected");
            }
        });
        tree.setItemStyleGenerator((Tree.ItemStyleGenerator) (source, itemId) -> {
            if (itemId instanceof GridCell) {
                if (!((GridCell) itemId).isAvailable()) {
                    if (source.getChildren(itemId) == null) {
                        return "not-available";
                    }
                }
            }
            return null;
        });
        tree.setDragMode(Tree.TreeDragMode.NODE);
        tree.addItem(rootDashboardPanel);
        tree.setItemCaption(rootDashboardPanel, "Root container");
        tree.setItemIcon(rootDashboardPanel, FontAwesome.ASTERISK);
        tree.setChildrenAllowed(rootDashboardPanel, false);

        rootDashboardPanel.setSpacing(true);
        rootDashboardPanel.setMargin(true);
        DDVerticalLayoutDropHandler ddVerticalLayoutDropHandler = new DDVerticalLayoutDropHandler();
        ddVerticalLayoutDropHandler.setGridDropListener(this::onGridDrop);
        ddVerticalLayoutDropHandler.setComponentDescriptorTree(tree);
        rootDashboardPanel.setDropHandler(ddVerticalLayoutDropHandler);
        rootDashboardPanel.addLayoutClickListener(
                (LayoutEvents.LayoutClickListener) e ->
                        tree.setValue(e.getClickedComponent())
        );
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
        Slider cols = new Slider();
        Slider rows = new Slider();

        cols.setMin(1);
        cols.setMax(10);

        rows.setMin(1);
        rows.setMax(10);

        cols.setValue((double) 2);
        rows.setValue((double) 2);
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
                DashboardUtils.removeEmptyLabels(gridLayout, tree);
                gridLayout.setColumns(cols.getValue().intValue());
                gridLayout.setRows(rows.getValue().intValue());
                DashboardUtils.addEmptyLabels(gridLayout, tree);
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

    private void setupWidgetsPalette(DDCssLayout containersDraggableLayout) {
        PaletteButton verticalLayoutButton = new PaletteButton("Vertical", FontAwesome.ARROWS_V);
        verticalLayoutButton.setWidgetType(WidgetType.VERTICAL_LAYOUT);
        verticalLayoutButton.setWidth("100%");
        verticalLayoutButton.setHeight("50px");
        verticalLayoutButton.setStyleName("dd-palette-button");

        PaletteButton horizontalLayoutButton = new PaletteButton("Horizontal", FontAwesome.ARROWS_H);
        horizontalLayoutButton.setWidgetType(WidgetType.HORIZONTAL_LAYOUT);
        horizontalLayoutButton.setWidth("100%");
        horizontalLayoutButton.setHeight("50px");
        horizontalLayoutButton.setStyleName("dd-palette-button");

        PaletteButton gridButton = new PaletteButton("Grid", FontAwesome.TH);
        gridButton.setWidgetType(WidgetType.GRID_LAYOUT);
        gridButton.setWidth("100%");
        gridButton.setHeight("50px");
        gridButton.setStyleName("dd-palette-button");

        containersDraggableLayout.addComponent(verticalLayoutButton);
        containersDraggableLayout.addComponent(horizontalLayoutButton);
        containersDraggableLayout.addComponent(gridButton);

        for (WidgetRepository.Widget widget : widgetRepository.getWidgets()) {
            PaletteButton paletteButton = new PaletteButton(
                    messages.getTools().loadString(widget.getCaption()), // todo msg://
                    WebComponentsHelper.getIcon(widget.getIcon())
            );

            paletteButton.setWidgetType(WidgetType.FRAME_PANEL);
            paletteButton.setWidth("100%");
            paletteButton.setHeight("50px");
            paletteButton.setStyleName("dd-palette-button");
            paletteButton.setDropFrame(getFrame());
            paletteButton.setWidget(widget);
            containersDraggableLayout.addComponent(paletteButton);
        }
    }

    private void removeAllComponents() {
        for (Object child : tree.getChildren(rootDashboardPanel)) {
            ((HierarchicalContainer) tree.getContainerDataSource()).removeItemRecursively(child);
        }
        tree.setChildrenAllowed(rootDashboardPanel, false);
        ((TreeDropHandler) tree.getDropHandler()).getTreeChangeListener().treeChanged();
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

    public void removeComponent() {
        boolean removed = TreeUtils.removeComponent(tree, tree.getValue());
        ((TreeDropHandler) tree.getDropHandler()).getTreeChangeListener().treeChanged();
        if (!removed) {
            showNotification("You can't remove the root component", NotificationType.HUMANIZED);
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
                        removeAllComponents();
                    }
                },
                new DialogAction(DialogAction.Type.NO) {
                    @Override
                    public void actionPerform(com.haulmont.cuba.gui.components.Component component) {
                    }
                }
        });
    }
}