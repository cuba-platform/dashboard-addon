/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.layouts;

import com.haulmont.addon.dnd.web.gui.components.WebDDHorizontalLayout;

public class CanvasHorizontalLayout extends AbstractCanvasLayout {
    protected WebDDHorizontalLayout horizontalLayout;

    public CanvasHorizontalLayout() {
        super(new WebDDHorizontalLayout());
        horizontalLayout = (WebDDHorizontalLayout) delegate;
    }

    @Override
    public WebDDHorizontalLayout getDelegate() {
        return horizontalLayout;
    }
}
