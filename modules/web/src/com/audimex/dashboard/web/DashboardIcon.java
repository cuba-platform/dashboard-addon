/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web;

import com.haulmont.cuba.gui.icons.Icons;

public enum DashboardIcon implements Icons.Icon {
    VERTICAL_LAYOUT_ICON("icons/vertical-container.png"),
    HORIZONTAL_LAYOUT_ICON("icons/horizontal-container.png"),
    GRID_LAYOUT_ICON("icons/grid-container.png");

    protected String source;

    DashboardIcon(String source) {
        this.source = source;
    }

    @Override
    public String source() {
        return source;
    }
}
