/*
 * Copyright (c) 2008-2018 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.haulmont.addon.dashboard.web.dashboard.tools;

import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.model.visualmodel.*;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame;
import com.haulmont.addon.dashboard.web.dashboard.layouts.*;
import com.haulmont.addon.dashboard.web.dashboard.tools.componentfactory.CanvasComponentsFactory;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.ComponentContainer;
import com.haulmont.cuba.gui.components.ExpandingLayout;
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
            canvasLayout = factory.createCanvasRootLayout((RootLayout) model);
        } else if (model instanceof VerticalLayout) {
            canvasLayout = factory.createCanvasVerticalLayout((VerticalLayout) model);
        } else if (model instanceof HorizontalLayout) {
            canvasLayout = factory.createCanvasHorizontalLayout((HorizontalLayout) model);
        } else if (model instanceof CssLayout) {
            CssLayout cssLayoutModel = (CssLayout) model;
            canvasLayout = factory.createCssLayout(cssLayoutModel);
        } else if (model instanceof WidgetLayout) {
            canvasLayout = factory.createCanvasWidgetLayout(frame, ((WidgetLayout) model));
        } else if (model instanceof GridLayout) {
            GridLayout gridModel = (GridLayout) model;
            canvasLayout = factory.createCanvasGridLayout(gridModel);

            for (GridArea area : gridModel.getAreas()) {
                GridCellLayout cellLayout = area.getComponent();
                CanvasLayout childGridCanvas = modelToContainer(frame, cellLayout);
                Integer col2 = area.getCol() + cellLayout.getColSpan();
                Integer row2 = area.getRow() + cellLayout.getRowSpan();
                ((CanvasGridLayout) canvasLayout).addComponent(childGridCanvas, area.getCol(), area.getRow(), col2, row2);
            }
        }

        if (canvasLayout == null) {
            throw new IllegalStateException("Unknown layout class: " + model.getClass());
        }

        ComponentContainer delegate = canvasLayout.getDelegate();

        if (model.getStyleName() != null) {
            canvasLayout.addStyleName(model.getStyleName());
        }
        canvasLayout.setWidth(model.getWidthWithUnits());
        canvasLayout.setHeight(model.getHeightWithUnits());

        if (!(canvasLayout instanceof CanvasGridLayout)) {

            boolean expanded = isExpanded(model);
            if (!model.getChildren().isEmpty()) {

                for (DashboardLayout childModel : model.getChildren()) {
                    CanvasLayout childContainer = modelToContainer(frame, childModel);
                    delegate.add(childContainer);

                    if (childModel.getId().equals(model.getExpand())) {
                        if (delegate instanceof ExpandingLayout) {
                            ((ExpandingLayout) delegate).expand(childContainer);
                        }
                    }

                    if (!expanded) {
                        childContainer.setWeight(childModel.getWeight());
                    }
                }
            }
        }

        if (model.getUuid() != null) {
            canvasLayout.setUuid(model.getUuid());
        }
        return canvasLayout;
    }

    private boolean isExpanded(DashboardLayout model) {
        return model.getExpand() != null && model.getChildren().stream()
                .anyMatch(e -> model.getExpand().equals(e.getId()));
    }

    protected void containerToModel(DashboardLayout model, Component container) {
        if (container instanceof CanvasLayout) {
            CanvasLayout canvasLayout = (CanvasLayout) container;
            model.setUuid(canvasLayout.getUuid() == null ? UUID.randomUUID() : canvasLayout.getUuid());
        }
        if (container instanceof HasWeight) {
            model.setWeight(((HasWeight) container).getWeight());
        }

        for (Component childComponent : ((ComponentContainer) container).getOwnComponents()) {
            DashboardLayout childModel = createDashboardLayout(childComponent);

            if (childModel == null && childComponent instanceof ComponentContainer) {
                containerToModel(model, childComponent);
            } else if (childModel instanceof GridLayout) {
                GridLayout gridModel = (GridLayout) childModel;
                com.haulmont.cuba.gui.components.GridLayout gridComponent = ((CanvasGridLayout) childComponent).getDelegate();
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
                    modelArea.setComponent((GridCellLayout) modelChildGridArea);
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
            com.haulmont.cuba.gui.components.GridLayout gridLayout = ((CanvasGridLayout) component).getDelegate();
            layout.setRows(gridLayout.getRows());
            layout.setColumns(gridLayout.getColumns());
            return layout;
        }
        return null;
    }
}