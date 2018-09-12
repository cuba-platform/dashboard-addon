/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.events;

import com.haulmont.addon.dashboard.model.visualmodel.DashboardLayout;

import java.util.UUID;

public class WidgetMovedEvent extends WidgetTreeEvent {

    public WidgetMovedEvent(DashboardLayout source, UUID parentLayoutUuid, String location, int index) {
        super(source, parentLayoutUuid, location, index);
    }

}
