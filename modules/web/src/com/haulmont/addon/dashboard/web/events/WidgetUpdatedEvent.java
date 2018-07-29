/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.events;

import com.haulmont.addon.dashboard.model.Widget;

public class WidgetUpdatedEvent extends DashboardEvent {

    public WidgetUpdatedEvent(Widget source) {
        super(source);
    }

    @Override
    public Widget getSource() {
        return (Widget) super.getSource();
    }
}
