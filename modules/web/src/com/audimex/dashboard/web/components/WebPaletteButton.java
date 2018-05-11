package com.audimex.dashboard.web.components;

import com.audimex.dashboard.gui.components.PaletteButton;
import com.audimex.dashboard.model.visual_model.DashboardLayout;
import com.haulmont.cuba.web.gui.components.WebButton;

public class WebPaletteButton extends WebButton implements PaletteButton {
    protected DashboardLayout layout;

    public WebPaletteButton() {

    }

    public DashboardLayout getLayout() {
        return layout;
    }

    public void setLayout(DashboardLayout layout) {
        this.layout = layout;
    }
}
