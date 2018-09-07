/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.events;

import com.haulmont.addon.dashboard.model.visual_model.DashboardLayout;
import com.vaadin.shared.ui.dd.VerticalDropLocation;

import java.util.UUID;

public class WidgetAddedEvent extends WidgetTreeEvent {

    public WidgetAddedEvent(DashboardLayout source, UUID parentLayoutUuid, VerticalDropLocation location) {
        super(source, parentLayoutUuid, location);
    }
}
