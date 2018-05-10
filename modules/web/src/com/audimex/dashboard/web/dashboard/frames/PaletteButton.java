/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.frames;

import com.audimex.dashboard.model.visual_model.LayoutType;
import com.audimex.dashboard.web.DashboardStyleConstants;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.icons.Icons;
import com.haulmont.cuba.web.gui.components.WebButton;
import com.vaadin.server.Resource;

import java.util.UUID;

public class PaletteButton extends WebButton implements Button {
    protected LayoutType layoutType;
    protected UUID widgetUuid;

    public PaletteButton() {

    }

    public PaletteButton(String caption, Icons.Icon icon, LayoutType layoutType, UUID widgetUuid) {
        this.setCaption(caption);
        this.setIconFromSet(icon);
        this.layoutType = layoutType;
        this.widgetUuid = widgetUuid;
        this.setWidth("100%");
        this.setHeight("50px");
        this.setStyleName(DashboardStyleConstants.AMXD_DASHBOARD_BUTTON);
    }

    public PaletteButton(String caption, String icon, LayoutType layoutType, UUID widgetUuid) {
        this.setCaption(caption);
        this.setIcon(icon);
        this.layoutType = layoutType;
        this.widgetUuid = widgetUuid;
        this.setWidth("100%");
        this.setHeight("50px");
        this.setStyleName(DashboardStyleConstants.AMXD_DASHBOARD_BUTTON);
    }

    public LayoutType getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(LayoutType layoutType) {
        this.layoutType = layoutType;
    }

    public UUID getWidgetUuid() {
        return widgetUuid;
    }

    public void setWidgetUuid(UUID widgetUuid) {
        this.widgetUuid = widgetUuid;
    }
}
