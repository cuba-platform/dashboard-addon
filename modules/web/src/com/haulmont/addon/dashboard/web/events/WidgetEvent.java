/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.events;

import com.haulmont.addon.dashboard.model.Widget;

public class WidgetEvent extends DashboardEvent {
    public WidgetEvent(Widget source) {
        super(source);
    }
}
