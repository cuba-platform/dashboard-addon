/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.visual_model;

import java.util.UUID;

public class FramePanel extends DashboardLayout {
    protected UUID widgetId;

    public UUID getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(UUID widgetId) {
        this.widgetId = widgetId;
    }
}
