/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.events;

import com.haulmont.addon.dashboard.model.Dashboard;

public class DashboardUpdatedEvent extends DashboardEvent {

    public DashboardUpdatedEvent(Dashboard source) {
        super(source);
    }

    @Override
    public Dashboard getSource() {
        return (Dashboard) super.getSource();
    }
}
