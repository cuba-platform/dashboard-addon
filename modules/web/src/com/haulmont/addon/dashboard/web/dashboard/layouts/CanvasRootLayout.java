/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.layouts;

import com.haulmont.addon.dashboard.model.visualmodel.RootLayout;
import com.haulmont.addon.dnd.web.gui.components.WebDDVerticalLayout;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.components.ScrollBoxLayout;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;

public class CanvasRootLayout extends AbstractCanvasLayout {
    protected WebDDVerticalLayout verticalLayout;

    public CanvasRootLayout(RootLayout model) {
        super(model, AppBeans.get(ComponentsFactory.class).createComponent(ScrollBoxLayout.class));
        ScrollBoxLayout scrollBoxLayout = (ScrollBoxLayout) delegate;
        scrollBoxLayout.setSizeFull();
        verticalLayout = new WebDDVerticalLayout();
        scrollBoxLayout.add(verticalLayout);
    }

    @Override
    public WebDDVerticalLayout getDelegate() {
        return verticalLayout;
    }
}
