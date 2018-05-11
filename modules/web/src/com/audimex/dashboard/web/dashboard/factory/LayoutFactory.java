/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.factory;

import com.audimex.dashboard.gui.Draggable;
import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.model.visual_model.*;
import com.haulmont.addon.dnd.components.DDGridLayout;
import com.haulmont.addon.dnd.components.DDHorizontalLayout;
import com.haulmont.addon.dnd.components.DDVerticalLayout;
import com.haulmont.addon.dnd.components.DropHandler;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public abstract class LayoutFactory {
    @Inject
    protected ComponentsFactory componentsFactory;

    public com.haulmont.cuba.gui.components.Component createComponent(Draggable component) {
        DashboardLayout layout = component.getLayout();

        if (layout instanceof VerticalLayout) {
            return createVerticalLayout();
        } else if (layout instanceof HorizontalLayout) {
            return createHorizontalLayout();
        } else if (layout instanceof GridLayout) {
            return createGridLayout();
        } else if (layout instanceof GridArea) {
            return null;
//            return createGridArea();
        } else if (layout instanceof WidgetLayout) {
            return createWidgetLayout(((WidgetLayout) layout).getWidget());
        }
        return null;
    }

    public abstract DropHandler getVerticalDropHandler();
    public abstract DropHandler getHorizontalDropHandler();

    protected DDVerticalLayout createVerticalLayout() {
        DDVerticalLayout verticalLayout = componentsFactory.createComponent(DDVerticalLayout.class);
        verticalLayout.setMargin(true);
        verticalLayout.setSizeFull();
        verticalLayout.setStyleName("amxd-shadow-border");
        verticalLayout.setDropHandler(getVerticalDropHandler());
        return verticalLayout;
    }

    protected DDHorizontalLayout createHorizontalLayout() {
        DDHorizontalLayout horizontalLayout = componentsFactory.createComponent(DDHorizontalLayout.class);
        horizontalLayout.setMargin(true);
        horizontalLayout.setSizeFull();
        horizontalLayout.setStyleName("amxd-shadow-border");
        horizontalLayout.setDropHandler(getHorizontalDropHandler());
        return horizontalLayout;
    }


    protected AbstractFrame createWidgetLayout(Widget widget) {
        return null;
    }

    protected DDGridLayout createGridLayout() {
        return null;
    }
}
