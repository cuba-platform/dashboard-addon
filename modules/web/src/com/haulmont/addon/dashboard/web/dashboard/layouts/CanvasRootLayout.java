/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.layouts;

import com.haulmont.addon.dashboard.model.visualmodel.RootLayout;
import com.haulmont.cuba.gui.components.VBoxLayout;

import static com.haulmont.addon.dashboard.web.DashboardStyleConstants.DASHBOARD_ROOT_LAYOUT;

public class CanvasRootLayout extends AbstractCanvasLayout {
    protected VBoxLayout verticalLayout;

    public CanvasRootLayout(RootLayout model) {
        super(model, VBoxLayout.class);
        verticalLayout = (VBoxLayout) delegate;
        delegate.setStyleName(DASHBOARD_ROOT_LAYOUT);
    }

    @Override
    public VBoxLayout getDelegate() {
        return verticalLayout;
    }
}
