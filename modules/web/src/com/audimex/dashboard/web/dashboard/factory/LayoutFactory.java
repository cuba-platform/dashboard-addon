/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.factory;

import com.audimex.dashboard.model.visual_model.LayoutType;
import com.haulmont.addon.dnd.components.DDGridLayout;
import com.haulmont.addon.dnd.components.DDHorizontalLayout;
import com.haulmont.addon.dnd.components.DDVerticalLayout;
import com.haulmont.addon.dnd.components.DropHandler;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static com.audimex.dashboard.model.visual_model.LayoutType.VERTICAL_LAYOUT;

@Component
public abstract class LayoutFactory {
    @Inject
    protected ComponentsFactory componentsFactory;

    public com.haulmont.cuba.gui.components.Component createLayout(LayoutType layoutType) {
        switch (layoutType) {
            case VERTICAL_LAYOUT:
                return createVerticalLayout();
            case HORIZONTAL_LAYOUT:
                return createHorizontalLayout();
            case GRID_LAYOUT:
                return createGridLayout();
            case FRAME_PANEL:
                return null;
            case GRID_AREA:
                return null;
            default:
                return null;
        }
    }

    public abstract DropHandler getDropHandler(LayoutType type);

    protected DDVerticalLayout createVerticalLayout() {
        DDVerticalLayout verticalLayout = componentsFactory.createComponent(DDVerticalLayout.class);
        verticalLayout.setMargin(true);
        verticalLayout.setHeightFull();
        verticalLayout.setStyleName("amxd-shadow-border");
        verticalLayout.setDropHandler(getDropHandler(VERTICAL_LAYOUT));
        return verticalLayout;
    }

    protected DDHorizontalLayout createHorizontalLayout() {
        return null;
    }

    protected DDGridLayout createGridLayout() {
        return null;
    }
}
