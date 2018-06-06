/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.frames.editor.vaadin_components;

import com.audimex.dashboard.model.visual_model.DashboardLayout;
import com.vaadin.ui.Button;

public class PaletteButton extends Button implements Draggable {
    protected DashboardLayout layout;

    public DashboardLayout getLayout() {
        return layout;
    }

    public void setLayout(DashboardLayout layout) {
        this.layout = layout;
    }
}
