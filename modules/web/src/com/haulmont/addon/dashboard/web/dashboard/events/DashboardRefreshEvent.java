/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.events;

import com.haulmont.addon.dashboard.model.visual_model.DashboardLayout;

import java.util.UUID;

public class DashboardRefreshEvent extends DashboardEditEvent {

    private UUID selectId;

    public DashboardRefreshEvent(DashboardLayout source) {
        super(source);
    }

    public DashboardRefreshEvent(Object source, UUID selectId) {
        super(source);
        this.selectId = selectId;
    }

    @Override
    public DashboardLayout getSource() {
        return (DashboardLayout) super.getSource();
    }

    public UUID getSelectId() {
        return selectId;
    }
}
