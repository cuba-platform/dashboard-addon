/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.events;

import com.audimex.dashboard.model.Dashboard;
import com.haulmont.cuba.gui.events.UiEvent;
import org.springframework.context.ApplicationEvent;

public class DashboardUpdatedEvent extends ApplicationEvent implements UiEvent {

    public DashboardUpdatedEvent(Dashboard source) {
        super(source);
    }

    @Override
    public Dashboard getSource() {
        return (Dashboard) super.getSource();
    }
}
