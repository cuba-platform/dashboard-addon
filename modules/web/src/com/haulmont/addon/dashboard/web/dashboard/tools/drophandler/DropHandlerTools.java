/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.tools.drophandler;

import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.web.dashboard.layouts.*;
import com.haulmont.cuba.gui.components.Component;

public class DropHandlerTools {

    protected Dashboard dashboard;

    public DropHandlerTools(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public void initDropHandler(Component component) {
        if (component instanceof CanvasLayout) {
            addDropHandler((CanvasLayout) component);
        }
        if (component instanceof Component.Container) {
            for (Component child : ((Component.Container) component).getOwnComponents()) {
                initDropHandler(child);
            }
        }
    }

    public void addDropHandler(CanvasLayout layout) {
        if (layout instanceof CanvasVerticalLayout) {
            layout.setDropHandler(new LayoutDropHandler(dashboard));
        } else if (layout instanceof CanvasHorizontalLayout) {
            layout.setDropHandler(new LayoutDropHandler(dashboard));
        } else if (layout instanceof CanvasGridLayout) {
            layout.setDropHandler(new NotDropHandler());
        } else if (layout instanceof CanvasWidgetLayout) {
            layout.setDropHandler(new NotDropHandler());
        }
    }

}
