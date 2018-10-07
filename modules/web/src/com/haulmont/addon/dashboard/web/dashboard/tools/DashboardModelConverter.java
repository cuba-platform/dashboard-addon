/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.tools;

import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.model.visualmodel.*;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame;
import com.haulmont.addon.dashboard.web.dashboard.layouts.*;
import com.haulmont.addon.dashboard.web.dashboard.tools.componentfactory.CanvasComponentsFactory;
import com.haulmont.addon.dnd.components.DDGridLayout;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Component.Container;
import com.haulmont.cuba.gui.components.GridLayout.Area;

import javax.inject.Inject;
import java.util.UUID;

public class DashboardModelConverter {
    @Inject
    protected Metadata metadata;

    protected CanvasComponentsFactory factory;

    public CanvasComponentsFactory getFactory() {
        return factory;
    }

    @Inject
    public void setFactory(CanvasComponentsFactory factory) {
        this.factory = factory;
    }

    public RootLayout containerToModel(CanvasVerticalLayout container) {
        RootLayout model = metadata.create(RootLayout.class);
        containerToModel(model, container);
        return model;
    }

    public CanvasLayout modelToContainer(CanvasFrame frame, DashboardLayout model) {
        CanvasLayout canvasLayout = null;
        if (model instanceof RootLayout) {
            canvasLayout = factory.createCanvasRootLayout();
        } else if (model instanceof VerticalLayout) {
            canvasLayout = factory.createCanvasVerticalLayout();
        } else if (model instanceof HorizontalLayout) {
            canvasLayout = factory.createCanvasHorizontalLayout();
        } else if (model instanceof WidgetLayout) {
            canvasLayout = factory.createCanvasWidgetLayout(frame, ((WidgetLayout) model).getWidget());
        } else if (model instanceof GridLayout) {
            GridLayout gridModel = (GridLayout) model;
            Integer columns = gridModel.getColumns();
            Integer rows = gridModel.getRows();
            canvasLayout = factory.createCanvasGridLayout(columns, rows);

            for (GridArea area : gridModel.getAreas()) {
                CanvasLayout childGridCanvas = modelToContainer(frame, area.getComponent());
                ((CanvasGridLayout) canvasLayout).addComponent(childGridCanvas, area.getCol(), area.getRow());
            }
        }

        if (canvasLayout != null && !(canvasLayout instanceof CanvasGridLayout)) {
            for (DashboardLayout childModel : model.getChildren()) {
                CanvasLayout childContainer = modelToContainer(frame, childModel);
                ((Container) canvasLayout.getDelegate()).add(childContainer);
                childContainer.setWeight(childModel.getWeight());
            }
        }

        if (canvasLayout != null && model.getUuid() != null) {
            canvasLayout.setUuid(model.getUuid());
        }
        return canvasLayout;
    }

    protected void containerToModel(DashboardLayout model, Component container) {
        if (container instanceof AbstractCanvasLayout) {
            model.setUuid(((AbstractCanvasLayout) container).getUuid() == null ? UUID.randomUUID() : ((AbstractCanvasLayout) container).getUuid());
        }
        if (container instanceof HasWeight) {
            model.setWeight(((HasWeight) container).getWeight());
        }

        for (Component childComponent : ((Container) container).getOwnComponents()) {
            DashboardLayout childModel = createDashboardLayout(childComponent);

            if (childModel == null && childComponent instanceof Container) {
                containerToModel(model, childComponent);
            } else if (childModel instanceof GridLayout) {
                GridLayout gridModel = (GridLayout) childModel;
                DDGridLayout gridComponent = ((CanvasGridLayout) childComponent).getDelegate();
                model.addChild(gridModel);
                childModel.setUuid((((CanvasGridLayout) childComponent).getUuid() == null ?
                        UUID.randomUUID() : ((CanvasGridLayout) childComponent).getUuid()));

                for (Component gridChild : gridComponent.getOwnComponents()) {
                    GridArea modelArea = metadata.create(GridArea.class);
                    Area area = gridComponent.getComponentArea(gridChild);

                    DashboardLayout modelChildGridArea = createDashboardLayout(gridChild);
                    containerToModel(modelChildGridArea, gridChild);

                    modelArea.setCol(area.getColumn1());
                    modelArea.setRow(area.getRow1());
                    modelArea.setComponent(modelChildGridArea);
                    gridModel.addArea(modelArea);
                }
            } else if (childModel != null) {
                model.addChild(childModel);
                if (!(childModel instanceof WidgetLayout)) {
                    containerToModel(childModel, childComponent);
                }
            }
        }
    }

    protected DashboardLayout createDashboardLayout(Component component) {
        if (component.getParent() == null) {
            return metadata.create(RootLayout.class);
        } else if (component.getParent() instanceof CanvasGridLayout) {
            return metadata.create(GridCellLayout.class);
        } else if (component instanceof CanvasVerticalLayout) {
            return metadata.create(VerticalLayout.class);
        } else if (component instanceof CanvasHorizontalLayout) {
            return metadata.create(HorizontalLayout.class);
        } else if (component instanceof CanvasWidgetLayout) {
            WidgetLayout layout = metadata.create(WidgetLayout.class);
            Widget widget = ((CanvasWidgetLayout) component).getWidget();
            layout.setWidget(widget);
            layout.setUuid(((CanvasWidgetLayout) component).getUuid() == null ? UUID.randomUUID() : ((CanvasWidgetLayout) component).getUuid());
            return layout;
        } else if (component instanceof CanvasGridLayout) {
            GridLayout layout = metadata.create(GridLayout.class);
            DDGridLayout gridLayout = ((CanvasGridLayout) component).getDelegate();
            layout.setRows(gridLayout.getRows());
            layout.setColumns(gridLayout.getColumns());
            return layout;
        }
        return null;
    }
}