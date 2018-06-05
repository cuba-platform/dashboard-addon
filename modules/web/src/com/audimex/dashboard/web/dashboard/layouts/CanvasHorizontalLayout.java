/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.layouts;

import fi.jasoft.dragdroplayouts.DDHorizontalLayout;

public class CanvasHorizontalLayout extends AbstractOrderedCanvasLayout {
    protected DDHorizontalLayout horizontalLayout;

    public CanvasHorizontalLayout() {
        super(new DDHorizontalLayout());
        horizontalLayout = (DDHorizontalLayout) delegate;
    }

    @Override
    public DDHorizontalLayout getDelegate() {
        return horizontalLayout;
    }
}
