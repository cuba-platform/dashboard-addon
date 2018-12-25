/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.layouts;

import com.haulmont.addon.dashboard.model.visualmodel.RootLayout;
import com.haulmont.cuba.gui.components.VBoxLayout;

import static com.haulmont.addon.dashboard.web.DashboardStyleConstants.DASHBOARD_ROOT_LAYOUT;

public class CanvasRootLayout extends AbstractCanvasLayout {

    public static final String NAME = "canvasRootLayout";

    protected VBoxLayout verticalLayout;

    public CanvasRootLayout init(RootLayout model) {
        init(model, VBoxLayout.class);
        verticalLayout = (VBoxLayout) delegate;
        delegate.setStyleName(DASHBOARD_ROOT_LAYOUT);
        return this;
    }

    @Override
    public VBoxLayout getDelegate() {
        return verticalLayout;
    }
}
