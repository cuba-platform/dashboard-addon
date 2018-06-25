/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.events;

import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.web.events.DashboardEvent;

public class DoWidgetTemplateEvent extends DashboardEvent {

    public DoWidgetTemplateEvent(Widget source) {
        super(source);
    }

    @Override
    public Widget getSource() {
        return (Widget) super.getSource();
    }
}
