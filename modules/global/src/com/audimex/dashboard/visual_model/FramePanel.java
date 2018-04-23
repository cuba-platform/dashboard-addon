/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.visual_model;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;

import java.util.UUID;

@MetaClass(name = "amxd$FramePanel")
public class FramePanel extends DashboardLayout {
    private static final long serialVersionUID = -7216551967546718634L;

    @MetaProperty
    protected UUID widgetId;

    public UUID getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(UUID widgetId) {
        this.widgetId = widgetId;
    }
}
