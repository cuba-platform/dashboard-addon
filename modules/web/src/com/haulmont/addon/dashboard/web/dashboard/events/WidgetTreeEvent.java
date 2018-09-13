/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.events;

import com.haulmont.addon.dashboard.model.visualmodel.DashboardLayout;

import java.util.UUID;

public class WidgetTreeEvent extends AbstractDashboardEditEvent {

    private UUID parentLayoutUuid;
    private DropLocation location;

    public enum DropLocation {
        TOP,
        BOTTOM,
        MIDDLE,
        LEFT,
        RIGHT;
    }

    public WidgetTreeEvent(DashboardLayout source, UUID parentLayoutUuid, String location) {
        super(source);
        this.parentLayoutUuid = parentLayoutUuid;
        this.location = DropLocation.valueOf(location);
    }

    @Override
    public DashboardLayout getSource() {
        return (DashboardLayout) super.getSource();
    }

    public UUID getParentLayoutUuid() {
        return parentLayoutUuid;
    }

    public DropLocation getLocation() {
        return location;
    }

}
