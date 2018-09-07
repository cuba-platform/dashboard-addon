/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.events;

import com.haulmont.addon.dashboard.model.visual_model.DashboardLayout;
import com.vaadin.shared.ui.dd.VerticalDropLocation;

import java.util.UUID;

public class WidgetTreeEvent extends DashboardEditEvent {

    private UUID parentLayoutUuid;
    private VerticalDropLocation location;

    public WidgetTreeEvent(DashboardLayout source, UUID parentLayoutUuid, VerticalDropLocation location) {
        super(source);
        this.parentLayoutUuid = parentLayoutUuid;
        this.location = location;
    }

    @Override
    public DashboardLayout getSource() {
        return (DashboardLayout) super.getSource();
    }

    public UUID getParentLayoutUuid() {
        return parentLayoutUuid;
    }

    public VerticalDropLocation getLocation() {
        return location;
    }
}
