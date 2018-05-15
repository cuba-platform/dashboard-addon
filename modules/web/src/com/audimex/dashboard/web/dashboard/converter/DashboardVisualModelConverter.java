/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.converter;

import com.audimex.dashboard.model.visual_model.DashboardLayout;
import com.audimex.dashboard.model.visual_model.HorizontalLayout;
import com.audimex.dashboard.model.visual_model.VerticalLayout;
import com.audimex.dashboard.model.visual_model.WidgetLayout;
import com.audimex.dashboard.web.dashboard.factory.LayoutFactory;
import com.audimex.dashboard.web.widget_types.AbstractWidgetBrowse;
import com.haulmont.addon.dnd.components.DDHorizontalLayout;
import com.haulmont.addon.dnd.components.DDVerticalLayout;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Component.Container;
import com.haulmont.cuba.gui.components.VBoxLayout;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.audimex.dashboard.web.dashboard.converter.DashboardVisualModelConverter.NAME;
import static org.apache.commons.collections4.CollectionUtils.*;

@org.springframework.stereotype.Component(NAME)
public class DashboardVisualModelConverter {
    public static final String NAME = "amdx_DashboardVisualModelConverter";

    @Inject
    protected Metadata metadata;
    @Inject
    protected LayoutFactory layoutFactory;

    public LayoutFactory getLayoutFactory() {
        return layoutFactory;
    }

    public VerticalLayout convertVisualToModel(VBoxLayout rootContainer) {
        VerticalLayout model = metadata.create(VerticalLayout.class);

        if (isNotEmpty(rootContainer.getOwnComponents())) {
            model.setChildren(containerToModel(rootContainer));
        }
        return model;
    }

    public void convertModelToVisual(Container container, DashboardLayout model) {
        List<DashboardLayout> children = model.getChildren();

        for (DashboardLayout child : children) {
            Container childContainer = layoutFactory.createContainer(child);

            if (childContainer != null && isNotEmpty(child.getChildren())) {
                convertModelToVisual(childContainer, child);
            }

            if (childContainer != null) {
                container.add(childContainer);
            }
        }
    }

    protected List<DashboardLayout> containerToModel(Container container) {
        Collection<Component> children = container.getOwnComponents();
        List<DashboardLayout> models = new ArrayList<>();

        for (Component child : children) {
            DashboardLayout model = null;

            if (child instanceof DDVerticalLayout) {
                model = metadata.create(VerticalLayout.class);
            } else if (child instanceof DDHorizontalLayout) {
                model = metadata.create(HorizontalLayout.class);
            } else if (child instanceof AbstractWidgetBrowse) {
                model = metadata.create(WidgetLayout.class);
                ((WidgetLayout) model).setWidget(((AbstractWidgetBrowse) child).getWidget());
            }  //todo: add for grid layouts

            if (model != null && isNotEmpty(((Container) child).getOwnComponents())) {
                model.setChildren(containerToModel((Container) child));
            }

            if (model != null) {
                models.add(model);
            }
        }

        return models;
    }

}
