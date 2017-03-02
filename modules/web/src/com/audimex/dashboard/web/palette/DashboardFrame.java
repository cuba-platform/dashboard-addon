/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.palette;

import com.audimex.dashboard.entity.WidgetType;
import com.audimex.dashboard.web.drophandlers.GridDropListener;
import com.audimex.dashboard.web.drophandlers.TreeDropHandler;
import com.audimex.dashboard.web.layouts.DashboardHorizontalLayout;
import com.audimex.dashboard.web.layouts.DashboardVerticalLayout;
import com.audimex.dashboard.web.layouts.HasMainLayout;
import com.audimex.dashboard.web.model.DashboardModel;
import com.audimex.dashboard.web.model.WidgetModel;
import com.audimex.dashboard.web.repo.WidgetRepository;
import com.audimex.dashboard.web.settings.DashboardSettings;
import com.audimex.dashboard.web.utils.DashboardUtils;
import com.audimex.dashboard.web.utils.LayoutUtils;
import com.audimex.dashboard.web.utils.TreeUtils;
import com.audimex.dashboard.web.widgets.FramePanel;
import com.audimex.dashboard.web.widgets.GridCell;
import com.audimex.dashboard.web.widgets.GridRow;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.CheckBox;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.LayoutEvents;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;
import fi.jasoft.dragdroplayouts.DDCssLayout;
import fi.jasoft.dragdroplayouts.DragCaption;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;
import fi.jasoft.dragdroplayouts.interfaces.DragGrabFilter;
import fi.jasoft.dragdroplayouts.interfaces.LayoutDragSource;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DashboardFrame extends AbstractFrame {

    @Inject
    private VBoxLayout containers;

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

    protected DashboardVerticalLayout rootDashboardPanel = null;
    protected Component treeComponent;
    protected DashboardModel dashboardModel;
    private TreeDropHandler treeDropHandler = null;
    private GridDropListener gridDropListener = null;
    private DDCssLayout containersDraggableLayout = null;
    private DashboardMode mode;

    public enum DashboardMode {
        DESIGNER,
        VIEW
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        Layout containersLayout = (Layout) WebComponentsHelper.unwrap(containers);
        Layout dropLayoutContainer = (Layout) WebComponentsHelper.unwrap(dropLayout);
        Layout treeLayoutContainer = (Layout) WebComponentsHelper.unwrap(treeLayout);

        containersDraggableLayout = new DDCssLayout();
        containersDraggableLayout.setDragMode(DashboardUtils.getDefaultDragMode());
        containersDraggableLayout.setDragCaptionProvider(
                component -> new DragCaption(component.getCaption(), component.getIcon())
        );

        tree = new Tree();
        tree.setSizeFull();

        gridDropListener = (gridLayout, targetLayout, idx) -> onGridDrop(gridLayout, targetLayout, idx);
        treeDropHandler = new TreeDropHandler();
        treeDropHandler.setComponentDescriptorTree(tree);
        treeDropHandler.setGridDropListener(gridDropListener);
        treeDropHandler.setTreeChangeListener(tree -> TreeUtils.redrawLayout(tree, rootDashboardPanel));

        tree.setDropHandler(treeDropHandler);
        tree.addValueChangeListener(e -> {
            if (allowEdit.getValue()) {
                if (treeComponent != null) {
                    treeComponent.removeStyleName("tree-selected");
                }

                if (tree.getValue() == null) {
                    removeComponent.setEnabled(false);
                } else if (tree.getValue() != rootDashboardPanel) {
                    if (tree.getParent(tree.getValue()) != null) {
                        removeComponent.setEnabled(true);
                    } else {
                        removeComponent.setEnabled(false);
                    }

                    Object treeObject = tree.getValue();
                    if (treeObject instanceof Component) {
                        if (treeObject instanceof GridCell) {
                            removeComponent.setEnabled(false);
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
                        removeComponent.setEnabled(false);
                        treeComponent = ((GridRow) treeObject).getGridLayout();
                    }

                    if (treeComponent != null) {
                        treeComponent.addStyleName("tree-selected");
                    }
                }
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

        rootDashboardPanel = new DashboardVerticalLayout(tree, this::onGridDrop);

        tree.addItem(rootDashboardPanel);
        tree.setItemCaption(rootDashboardPanel, getMessage("rootContainer"));
        tree.setItemIcon(rootDashboardPanel, FontAwesome.ASTERISK);
        tree.setChildrenAllowed(rootDashboardPanel, false);

        rootDashboardPanel.setDragMode(DashboardUtils.getDefaultDragMode());
        rootDashboardPanel.setDragMode(DashboardUtils.getDefaultDragMode());
        rootDashboardPanel.setDragGrabFilter(
                (DragGrabFilter) component -> dashboardSettings.isComponentDraggable(component)
        );

        rootDashboardPanel.addLayoutClickListener(
                (LayoutEvents.LayoutClickListener) e -> {
                        Component component = e.getClickedComponent();
                        if (component != null) {
                            if (component.getParent() instanceof HasMainLayout) {
                                tree.setValue(component.getParent());
                            } else {
                                tree.setValue(component);
                            }
                        }
                }
        );

        rootDashboardPanel.setDragCaptionProvider(
                component -> new DragCaption("Component", null)
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
                rootDashboardPanel.setDragMode(DashboardUtils.getDefaultDragMode());
                removeSpacings(rootDashboardPanel, true);
                palette.setVisible(true);
                containersDraggableLayout.removeStyleName("dd-container-disabled");
                rootDashboardPanel.removeStyleName("ad-dashboard-disabled");
            } else {
                containersDraggableLayout.setDragMode(LayoutDragMode.NONE);
                rootDashboardPanel.setDragMode(LayoutDragMode.NONE);
                removeSpacings(rootDashboardPanel, false);
                palette.setVisible(false);
                containersDraggableLayout.addStyleName("dd-container-disabled");
                rootDashboardPanel.addStyleName("ad-dashboard-disabled");
            }

            removeComponent.setEnabled(allowEdit.getValue());
        });

        mode = (DashboardMode) params.get("mode");
        enableViewMode();
    }

    private void enableViewMode() {
        if (mode == DashboardMode.VIEW) {
            allowEdit.setVisible(false);
            containersDraggableLayout.setDragMode(LayoutDragMode.NONE);
            rootDashboardPanel.setDragMode(LayoutDragMode.NONE);
            removeSpacings(rootDashboardPanel, false);
            palette.setVisible(false);
            containersDraggableLayout.addStyleName("dd-container-disabled");
            rootDashboardPanel.addStyleName("ad-dashboard-disabled");
        }
    }

    public DashboardModel getDashboardModel() {
        convertToModel();
        return dashboardModel;
    }

    public void setDashboardModel(DashboardModel dashboardModel) {
        this.dashboardModel = dashboardModel;
        convertToTree();
        enableViewMode();
    }

    private void onGridDrop(GridLayout gridLayout, Object targetLayout, int idx) {
        Window subWindow = new Window(getMessage("gridSettings"));
        subWindow.setModal(true);
        subWindow.setResizable(false);
        subWindow.setWidth(300, Sizeable.Unit.PIXELS);
        VerticalLayout subContent = new VerticalLayout();
        subContent.setSpacing(true);
        subWindow.setContent(subContent);

        HorizontalLayout buttonsPanel = new HorizontalLayout();
        HorizontalLayout comboBoxPanel = new HorizontalLayout();
        buttonsPanel.setSpacing(true);
        comboBoxPanel.setSpacing(true);
        comboBoxPanel.setWidth(100, Sizeable.Unit.PERCENTAGE);
        Slider cols = new Slider();
        Slider rows = new Slider();

        cols.setMin(1);
        cols.setMax(10);

        rows.setMin(1);
        rows.setMax(10);

        cols.setValue((double) 2);
        rows.setValue((double) 2);
        cols.setWidth(100, Sizeable.Unit.PERCENTAGE);
        rows.setWidth(100, Sizeable.Unit.PERCENTAGE);
        cols.setCaption(getMessage("columns"));
        rows.setCaption(getMessage("rows"));
        cols.focus();

        comboBoxPanel.addComponent(cols);
        comboBoxPanel.addComponent(rows);
        subContent.addComponent(comboBoxPanel);
        subContent.addComponent(buttonsPanel);

        Button cancelButton = new Button(getMessage("cancel"), FontAwesome.CLOSE);
        Button okButton = new Button(getMessage("ok"), FontAwesome.CHECK);
        cancelButton.addClickListener(event -> {
            subWindow.close();
        });

        okButton.addClickListener(event -> {
            if (cols.getValue() != null && rows.getValue() != null) {
                TreeUtils.addComponent(tree, targetLayout, gridLayout, idx);
                DashboardUtils.removeEmptyLabels(gridLayout, tree);
                gridLayout.setColumns(cols.getValue().intValue());
                gridLayout.setRows(rows.getValue().intValue());
                DashboardUtils.addEmptyLabels(gridLayout, tree);
                subWindow.close();
                treeDropHandler.getTreeChangeListener().accept(tree);
            }
        });

        buttonsPanel.addComponent(okButton);
        buttonsPanel.addComponent(cancelButton);
        buttonsPanel.setComponentAlignment(cancelButton, com.vaadin.ui.Alignment.MIDDLE_RIGHT);
        buttonsPanel.setComponentAlignment(okButton, com.vaadin.ui.Alignment.MIDDLE_RIGHT);

        subWindow.center();
        UI.getCurrent().addWindow(subWindow);
    }

    private void setupWidgetsPalette(DDCssLayout containersDraggableLayout) {
        PaletteButton verticalLayoutButton = new PaletteButton(getMessage("vertical"), FontAwesome.ARROWS_V);
        verticalLayoutButton.setWidgetType(WidgetType.VERTICAL_LAYOUT);
        verticalLayoutButton.setWidth("100%");
        verticalLayoutButton.setHeight("50px");
        verticalLayoutButton.setDropFrame(getFrame());
        verticalLayoutButton.setStyleName("dd-palette-button");

        PaletteButton horizontalLayoutButton = new PaletteButton(getMessage("horizontal"), FontAwesome.ARROWS_H);
        horizontalLayoutButton.setWidgetType(WidgetType.HORIZONTAL_LAYOUT);
        horizontalLayoutButton.setWidth("100%");
        horizontalLayoutButton.setHeight("50px");
        horizontalLayoutButton.setDropFrame(getFrame());
        horizontalLayoutButton.setStyleName("dd-palette-button");

        PaletteButton gridButton = new PaletteButton(getMessage("grid"), FontAwesome.TH);
        gridButton.setWidgetType(WidgetType.GRID_LAYOUT);
        gridButton.setWidth("100%");
        gridButton.setHeight("50px");
        gridButton.setDropFrame(getFrame());
        gridButton.setStyleName("dd-palette-button");

        containersDraggableLayout.addComponent(verticalLayoutButton);
        containersDraggableLayout.addComponent(horizontalLayoutButton);
        containersDraggableLayout.addComponent(gridButton);

        for (WidgetRepository.Widget widget : widgetRepository.getWidgets()) {
            PaletteButton paletteButton = new PaletteButton(
                    messages.getMainMessage(widget.getCaption()),
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
        HierarchicalContainer hierarchicalContainer = (HierarchicalContainer) tree.getContainerDataSource();
        hierarchicalContainer.removeItemRecursively(rootDashboardPanel);
        hierarchicalContainer.addItem(rootDashboardPanel);

        tree.setChildrenAllowed(rootDashboardPanel, false);
        ((TreeDropHandler) tree.getDropHandler()).getTreeChangeListener().accept(tree);
    }

    private void removeSpacings(AbstractLayout component, boolean value) {
        if (!(component instanceof HorizontalLayout
                || component instanceof VerticalLayout)) {
            if (value) {
                component.addStyleName("dd-bordering");
            } else {
                component.removeStyleName("dd-bordering");
            }

            if (component instanceof HasMainLayout) {
                ((HasMainLayout) component).setMargin(value);
            } else if (component instanceof GridLayout) {
                ((GridLayout) component).setMargin(value);
            }
        }

        if (component instanceof LayoutDragSource) {
            LayoutDragSource layoutDragSource = (LayoutDragSource) component;
            Component parent = layoutDragSource.getParent();
            if (parent instanceof LayoutDragSource) {
                layoutDragSource.setDragMode(((LayoutDragSource) parent).getDragMode());
            }
        }

        for (int i = 0; i < component.getComponentCount(); i++) {
            if (component instanceof CssLayout) {
                if (!(component instanceof FramePanel)) {
                    Component child = ((CssLayout) component).getComponent(i);
                    if (child instanceof AbstractLayout) {
                        removeSpacings((AbstractLayout) child, value);
                    }
                }
            } else if (component instanceof AbstractOrderedLayout) {
                Component child = ((AbstractOrderedLayout) component).getComponent(i);
                if (child instanceof AbstractLayout) {
                    removeSpacings((AbstractLayout) child, value);
                }
            } else if (component instanceof GridLayout) {
                GridLayout grid = (GridLayout) component;
                for (int k = 0; k < grid.getRows(); k++) {
                    for (int j = 0; j < grid.getColumns(); j++) {
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
        ((TreeDropHandler) tree.getDropHandler()).getTreeChangeListener().accept(tree);
        if (!removed) {
            showNotification(messages.getMessage(getClass(), "cantRemove"), NotificationType.HUMANIZED);
        }
    }

    public void clearAll() {
        showOptionDialog(messages.getMessage(getClass(), "areYouSure"),
                messages.getMessage(getClass(), "allComponentsWillBeRemoved"),
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

    public void convertToModel() {
        DashboardModel dashboardModel = new DashboardModel();
        dashboardModel.setWidgets(containerToModel(rootDashboardPanel));
        this.dashboardModel = dashboardModel;
    }

    private List<WidgetModel> containerToModel(Object parent) {
        Collection<?> children = tree.getChildren(parent);
        List<WidgetModel> widgetModels = new ArrayList<>();
        if (children != null) {
            for (Object child : children) {
                WidgetModel widgetModel = new WidgetModel();
                if (child instanceof DashboardVerticalLayout) {
                    DashboardVerticalLayout layout = (DashboardVerticalLayout) child;

                    widgetModel.setType(WidgetType.VERTICAL_LAYOUT);
                    widgetModel.setWeight(layout.getWeight());
                } else if (child instanceof DashboardHorizontalLayout) {
                    DashboardHorizontalLayout layout = (DashboardHorizontalLayout) child;

                    widgetModel.setType(WidgetType.HORIZONTAL_LAYOUT);
                    widgetModel.setWeight(layout.getWeight());
                } else if (child instanceof GridLayout) {
                    GridLayout layout = (GridLayout) child;
                    widgetModel.setType(WidgetType.GRID_LAYOUT);
                    widgetModel.setGridColumnCount(layout.getColumns());
                    widgetModel.setGridRowCount(layout.getRows());
                    widgetModel.setWeight(1);
                } else if (child instanceof FramePanel) {
                    FramePanel framePanel = (FramePanel) child;

                    widgetModel.setType(WidgetType.FRAME_PANEL);
                    widgetModel.setFrameId(framePanel.getFrameId());
                    widgetModel.setWeight(framePanel.getWeight());
                } else if (child instanceof GridRow) {
                    GridRow gridRow = (GridRow) child;
                    widgetModel.setType(WidgetType.GRID_ROW);
                } else if (child instanceof GridCell) {
                    GridCell gridCell = (GridCell) child;

                    widgetModel.setType(WidgetType.GRID_CELL);
                    widgetModel.setColumn(gridCell.getColumn());
                    widgetModel.setRow(gridCell.getRow());
                    widgetModel.setColspan(gridCell.getColspan());
                    widgetModel.setRowspan(gridCell.getRowspan());
                }

                if (tree.getChildren(child) != null) {
                    widgetModel.setChildren(containerToModel(child));
                }

                widgetModels.add(widgetModel);
            }
        }

        return widgetModels;
    }

    public void convertToTree() {
        removeAllComponents();

        modelToContainer(
                dashboardModel.getWidgets(),
                rootDashboardPanel,
                gridDropListener
        );

        TreeUtils.redrawLayout(tree, rootDashboardPanel);
    }

    private void modelToContainer(List<WidgetModel> widgetModels,
                                          Object parent, GridDropListener gridDropListener) {
        int idx = 0;
        for (WidgetModel widgetModel : widgetModels) {
            Object component = null;
            boolean isNew = false;
            switch (widgetModel.getType()) {
                case VERTICAL_LAYOUT:
                    DashboardVerticalLayout verticalLayout =
                            (DashboardVerticalLayout) LayoutUtils.createVerticalDropLayout(tree, gridDropListener);

                    verticalLayout.setWeight(widgetModel.getWeight());
                    component = verticalLayout;
                    isNew = true;
                    break;
                case HORIZONTAL_LAYOUT:
                    DashboardHorizontalLayout horizontalLayout =
                            (DashboardHorizontalLayout) LayoutUtils.createHorizontalDropLayout(tree, gridDropListener);

                    horizontalLayout.setWeight(widgetModel.getWeight());
                    component = horizontalLayout;
                    isNew = true;
                    break;
                case FRAME_PANEL:
                    FramePanel framePanel = new FramePanel(tree);
                    framePanel.setParentFrame(getFrame());
                    framePanel.setContent(widgetModel.getFrameId());
                    framePanel.setSizeFull();

                    component = framePanel;
                    isNew = true;
                    break;
                case GRID_LAYOUT:
                    GridLayout gridLayout = (GridLayout) LayoutUtils.createGridDropLayout(tree, gridDropListener);
                    gridLayout.setColumns(widgetModel.getGridColumnCount());
                    gridLayout.setRows(widgetModel.getGridRowCount());

                    component = gridLayout;
                    isNew = true;
                    break;
                case GRID_ROW:
                    GridRow gridRow = LayoutUtils.getGridRow(idx, tree, (GridLayout) parent);
                    component = gridRow;
                    break;
                case GRID_CELL:
                    GridLayout parentGrid = ((GridRow) parent).getGridLayout();
                    GridCell gridCell = LayoutUtils.getGridCell(
                            tree,
                            tree.getChildren(parentGrid),
                            widgetModel.getColumn(),
                            widgetModel.getRow()
                    );

                    gridCell.setColspan(widgetModel.getColspan());
                    gridCell.setRowspan(widgetModel.getRowspan());
                    component = gridCell;
                    break;
                default:
                    break;
            }

            if (isNew) {
                TreeUtils.addComponent(tree, parent, component, idx);
            }

            if (!widgetModel.getChildren().isEmpty()) {
                modelToContainer(widgetModel.getChildren(), component, gridDropListener);
            }

            idx++;
        }
    }
}