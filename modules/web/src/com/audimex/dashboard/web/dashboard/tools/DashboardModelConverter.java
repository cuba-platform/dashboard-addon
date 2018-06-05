/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.tools;

import com.audimex.dashboard.model.Dashboard;
import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.model.visual_model.*;
import com.audimex.dashboard.web.dashboard.layouts.*;
import com.audimex.dashboard.web.dashboard.tools.component_factories.VaadinComponentsFactory;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.Frame;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HasComponents;
import fi.jasoft.dragdroplayouts.DDGridLayout;

import javax.inject.Inject;

public class DashboardModelConverter {
    @Inject
    protected Metadata metadata;

    protected VaadinComponentsFactory factory;

    public VaadinComponentsFactory getFactory() {
        return factory;
    }

    @Inject
    public void setFactory(VaadinComponentsFactory factory) {
        this.factory = factory;
    }

    public VerticalLayout containerToModel(CanvasVerticalLayout container) {
        VerticalLayout model = metadata.create(VerticalLayout.class);
        containerToModel(model, container);
        return model;
    }

    public CanvasLayout modelToContainer(Frame frame, DashboardLayout model, Dashboard dashboard) {
        CanvasLayout canvasLayout = null;

        if (model instanceof VerticalLayout) {
            canvasLayout = factory.createCanvasVerticalLayout();
        } else if (model instanceof HorizontalLayout) {
            canvasLayout = factory.createCanvasHorizontalLayout();
        } else if (model instanceof WidgetLayout) {
            canvasLayout = factory.createCanvasWidgetLayout(frame, ((WidgetLayout) model).getWidget(), dashboard);
        } else if (model instanceof GridLayout) {
            GridLayout gridModel = (GridLayout) model;
            Integer columns = gridModel.getColumns();
            Integer rows = gridModel.getRows();
            canvasLayout = factory.createCanvasGridLayout(columns, rows);

            for (GridArea area : gridModel.getAreas()) {
                CanvasLayout childGridCanvas = modelToContainer(frame, area.getComponent(), dashboard);
                ((CanvasGridLayout) canvasLayout).addComponent(childGridCanvas, area.getCol1(), area.getRow1());
            }
        }

        if (canvasLayout != null && !(canvasLayout instanceof CanvasGridLayout)) {
            for (DashboardLayout childModel : model.getChildren()) {
                CanvasLayout childContainer = modelToContainer(frame, childModel, dashboard);
                ((CssLayout) canvasLayout).addComponent(childContainer);
                childContainer.setWeight(childModel.getWeight());
            }
        }

        return canvasLayout;
    }

    protected void containerToModel(DashboardLayout model, Component container) {
        if (container instanceof HasWeight) {
            model.setWeight(((HasWeight) container).getWeight());
        }

        for (Component childComponent : ((HasComponents) container)) {
            DashboardLayout childModel = createDashboardLayout(childComponent);

            if (childModel == null && childComponent instanceof HasComponents) {
                containerToModel(model, childComponent);
            } else if (childModel instanceof GridLayout) {
                GridLayout gridModel = (GridLayout) childModel;
                DDGridLayout gridComponent = ((CanvasGridLayout) childComponent).getDelegate();
                model.addChild(gridModel);

                for (Component gridChild : gridComponent) {
                    GridArea modelArea = metadata.create(GridArea.class);
                    com.vaadin.ui.GridLayout.Area area = gridComponent.getComponentArea(gridChild);

                    DashboardLayout modelChildGridArea = createDashboardLayout(gridChild);
                    containerToModel(modelChildGridArea, gridChild);

                    modelArea.setCol1(area.getColumn1());
                    modelArea.setRow1(area.getRow1());
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
        if (component instanceof CanvasVerticalLayout) {
            return metadata.create(VerticalLayout.class);
        } else if (component instanceof CanvasHorizontalLayout) {
            return metadata.create(HorizontalLayout.class);
        } else if (component instanceof CanvasWidgetLayout) {
            WidgetLayout layout = metadata.create(WidgetLayout.class);
            Widget widget = ((CanvasWidgetLayout) component).getWidget();
            layout.setWidget(widget);
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