/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.frames.editor.components;

import com.haulmont.addon.dashboard.model.visualmodel.DashboardLayout;
import com.haulmont.cuba.web.gui.components.WebButton;

//todo add to ui-components
public class PaletteButton extends WebButton implements Draggable {
    protected DashboardLayout layout;

    public DashboardLayout getLayout() {
        return layout;
    }

    public void setLayout(DashboardLayout layout) {
        this.layout = layout;
    }
}
