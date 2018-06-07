/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.events;

import com.audimex.dashboard.model.Widget;

public class WidgetUpdatedEvent extends DashboardEvent {

    public WidgetUpdatedEvent(Widget source) {
        super(source);
    }

    @Override
    public Widget getSource() {
        return (Widget) super.getSource();
    }
}
