/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.frames.editor.vaadin_components;

import com.audimex.dashboard.model.visual_model.DashboardLayout;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;

import static com.audimex.dashboard.web.DashboardStyleConstants.AMXD_DASHBOARD_BUTTON;

public class PaletteButton extends Button implements Draggable {
    protected DashboardLayout layout;

    public PaletteButton() {
        this.setWidth("100%");
        this.setHeight("50px");
        this.setStyleName(AMXD_DASHBOARD_BUTTON);
    }

    public PaletteButton(String caption, DashboardLayout layout, String description) {
        this(caption, null, layout, description);
    }

    public PaletteButton(String caption, Resource icon, DashboardLayout layout, String description) {
        this.setCaption(caption);
        this.setIcon(icon);
        this.setDescription(description);
        this.setLayout(layout);
        this.setWidth("100%");
        this.setHeight("50px");
        this.setStyleName(AMXD_DASHBOARD_BUTTON);
    }

    public DashboardLayout getLayout() {
        return layout;
    }

    public void setLayout(DashboardLayout layout) {
        this.layout = layout;
    }
}
