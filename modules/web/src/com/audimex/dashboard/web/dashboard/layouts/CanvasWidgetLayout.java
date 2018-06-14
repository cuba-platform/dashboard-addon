/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.layouts;

import com.audimex.dashboard.model.Widget;
import com.haulmont.addon.dnd.web.gui.components.WebDDVerticalLayout;

public class CanvasWidgetLayout extends AbstractCanvasLayout {
    protected WebDDVerticalLayout verticalLayout;
    protected Widget widget = null;

    public CanvasWidgetLayout() {
        super(new WebDDVerticalLayout());
        verticalLayout = (WebDDVerticalLayout) delegate;
    }

    @Override
    public WebDDVerticalLayout getDelegate() {
        return verticalLayout;
    }

    public Widget getWidget() {
        return widget;
    }

    public void setWidget(Widget widget) {
        this.widget = widget;
    }
}
