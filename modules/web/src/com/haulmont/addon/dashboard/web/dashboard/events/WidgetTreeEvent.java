/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.events;

import com.haulmont.addon.dashboard.model.visualmodel.DashboardLayout;
import com.vaadin.shared.ui.dd.VerticalDropLocation;

import java.util.UUID;

public class WidgetTreeEvent extends DashboardEditEvent {

    private UUID parentLayoutUuid;
    private String location;
    private int index;

    public WidgetTreeEvent(DashboardLayout source, UUID parentLayoutUuid, String location, int index) {
        super(source);
        this.parentLayoutUuid = parentLayoutUuid;
        this.location = location;
        this.index = index;
    }

    @Override
    public DashboardLayout getSource() {
        return (DashboardLayout) super.getSource();
    }

    public UUID getParentLayoutUuid() {
        return parentLayoutUuid;
    }

    public String getLocation() {
        return location;
    }

    public int getIndex() {
        return index;
    }
}
