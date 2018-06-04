/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.vaadin_components.layouts;

import com.audimex.dashboard.model.Widget;
import fi.jasoft.dragdroplayouts.DDVerticalLayout;

public class CanvasWidgetLayout extends AbstractOrderedCanvasLayout {
    protected DDVerticalLayout verticalLayout;
    protected Widget widget = null;

    public CanvasWidgetLayout() {
        super(new DDVerticalLayout());
        verticalLayout = (DDVerticalLayout) delegate;
    }

    @Override
    public DDVerticalLayout getDelegate() {
        return verticalLayout;
    }

    public Widget getWidget() {
        return widget;
    }

    public void setWidget(Widget widget) {
        this.widget = widget;
    }
}
