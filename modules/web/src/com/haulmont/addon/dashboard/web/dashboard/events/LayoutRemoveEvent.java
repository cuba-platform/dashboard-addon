/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.events;

import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasLayout;
import com.haulmont.addon.dashboard.web.events.DashboardEvent;
import com.haulmont.addon.dashboard.web.events.DashboardEvent;

public class LayoutRemoveEvent extends DashboardEvent {

    public LayoutRemoveEvent(CanvasLayout source) {
        super(source);
    }

    @Override
    public CanvasLayout getSource() {
        return (CanvasLayout) super.getSource();
    }
}
