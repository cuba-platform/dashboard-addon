/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.tools;

import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.model.visual_model.*;
import com.audimex.dashboard.web.widget_types.AbstractWidgetBrowse;
import com.haulmont.addon.dnd.components.DDGridLayout;
import com.haulmont.addon.dnd.components.DDHorizontalLayout;
import com.haulmont.addon.dnd.components.DDVerticalLayout;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Component.Container;
import com.haulmont.cuba.gui.components.GridLayout.Area;
import com.haulmont.cuba.gui.components.VBoxLayout;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.apache.commons.collections4.CollectionUtils.*;

@org.springframework.stereotype.Component
public class DashboardModelConverter {
    @Inject
    protected Metadata metadata;
    @Inject
    protected LayoutFactory factory;

    public VerticalLayout containerToModel(VBoxLayout rootContainer) {
        VerticalLayout model = metadata.create(VerticalLayout.class);

        for (Component container : rootContainer.getOwnComponents()) {
            model.addChild(containerToModel((Container) container));
        }

        return model;
    }

    public Container modelToContainer(DashboardLayout model) {
//        List<DashboardLayout> children = model.getChildren();
//
//        for (DashboardLayout child : children) {
//            Container childContainer = factory.createContainer(child);
//
//            if (childContainer != null && isNotEmpty(child.getChildren())) {
//                convertModelToVisual(childContainer, child);
//            }
//
//            if (childContainer != null) {
//                container.add(childContainer);
//            }
//        }
        return null;
    }

    protected DashboardLayout containerToModel(Container container) {
        DashboardLayout model = null;

        if (container instanceof DDVerticalLayout) {
            model = metadata.create(VerticalLayout.class);
        } else if (container instanceof DDHorizontalLayout) {
            model = metadata.create(HorizontalLayout.class);
        } else if (container instanceof AbstractWidgetBrowse) {
            model = metadata.create(WidgetLayout.class);
            Widget widget = ((AbstractWidgetBrowse) container).getWidget();
            ((WidgetLayout) model).setWidget(widget);
        } else if (container instanceof DDGridLayout) {
            model = metadata.create(GridLayout.class);
            ((GridLayout) model).setRows(((DDGridLayout) container).getRows());
            ((GridLayout) model).setColumns(((DDGridLayout) container).getColumns());
        }

        if (model != null && !(model instanceof WidgetLayout)) {
            for (Component child : container.getOwnComponents()) {
                if (model instanceof GridLayout) {
                    GridArea gridArea = metadata.create(GridArea.class);

                    Area area = ((DDGridLayout) container).getComponentArea(child);
                    gridArea.setCol1(area.getColumn1());
                    gridArea.setRow1(area.getRow1());

                    gridArea.setComponent(containerToModel((Container) child));
                    ((GridLayout) model).addArea(gridArea);
                } else {
                    model.addChild(containerToModel((Container) child));
                }
            }
        }

        return model;
    }

}
