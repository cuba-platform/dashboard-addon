/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.layouts;

import com.haulmont.addon.dnd.web.gui.components.WebDDVerticalLayout;

public class CanvasVerticalLayout extends AbstractCanvasLayout {
    protected WebDDVerticalLayout verticalLayout;

    public CanvasVerticalLayout() {
        super(new WebDDVerticalLayout());
        verticalLayout = (WebDDVerticalLayout) delegate;
    }

    @Override
    public WebDDVerticalLayout getDelegate() {
        return verticalLayout;
    }
}
