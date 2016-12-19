/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.utils;

import com.audimex.dashboard.entity.ComponentType;
import com.vaadin.ui.Component;
import fi.jasoft.dragdroplayouts.DDGridLayout;
import fi.jasoft.dragdroplayouts.DDHorizontalLayout;
import fi.jasoft.dragdroplayouts.DDVerticalLayout;

public class DashboardUtils {
    private DashboardUtils() {
    }

    public static ComponentType getTypeByComponent(Component component) {
        ComponentType componentType;
        if (component instanceof DDVerticalLayout) {
            componentType = ComponentType.VERTICAL_LAYOUT;
        } else if (component instanceof DDHorizontalLayout) {
            componentType = ComponentType.HORIZONTAL_LAYOUT;
        } else if (component instanceof DDGridLayout) {
            componentType = ComponentType.GRID_LAYOUT;
        } else {
            componentType = ComponentType.WIDGET;
        }
        
        return componentType;
    }
}