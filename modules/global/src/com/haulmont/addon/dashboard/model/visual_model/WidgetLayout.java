/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.model.visual_model;

import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;

@MetaClass(name = "amxd$WidgetLayout")
public class WidgetLayout extends DashboardLayout {

    public WidgetLayout() {
        caption = "Widget layout";
    }

    @MetaProperty
    protected Widget widget;

    public Widget getWidget() {
        return widget;
    }

    public void setWidget(Widget widget) {
        this.widget = widget;
    }
}
