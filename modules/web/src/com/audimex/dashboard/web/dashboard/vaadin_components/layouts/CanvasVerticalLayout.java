/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.vaadin_components.layouts;

import fi.jasoft.dragdroplayouts.DDVerticalLayout;

public class CanvasVerticalLayout extends AbstractOrderedCanvasLayout {
    protected DDVerticalLayout verticalLayout;

    public CanvasVerticalLayout() {
        super(new DDVerticalLayout());
        verticalLayout = (DDVerticalLayout) delegate;
    }

    @Override
    public DDVerticalLayout getDelegate() {
        return verticalLayout;
    }
}
