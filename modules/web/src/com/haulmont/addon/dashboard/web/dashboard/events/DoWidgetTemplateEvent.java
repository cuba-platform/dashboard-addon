/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.events;

import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.web.events.DashboardEvent;
import com.haulmont.addon.dashboard.web.events.DashboardEvent;

public class DoWidgetTemplateEvent extends DashboardEvent {

    public DoWidgetTemplateEvent(Widget source) {
        super(source);
    }

    @Override
    public Widget getSource() {
        return (Widget) super.getSource();
    }
}
