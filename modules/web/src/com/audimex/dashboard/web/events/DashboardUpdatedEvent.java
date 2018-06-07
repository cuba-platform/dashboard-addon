/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.events;

import com.audimex.dashboard.model.Dashboard;

public class DashboardUpdatedEvent extends DashboardEvent {

    public DashboardUpdatedEvent(Dashboard source) {
        super(source);
    }

    @Override
    public Dashboard getSource() {
        return (Dashboard) super.getSource();
    }
}
