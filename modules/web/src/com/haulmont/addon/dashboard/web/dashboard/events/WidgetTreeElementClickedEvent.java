/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.events;

import com.haulmont.addon.dashboard.web.dashboard.events.DashboardEditEvent;
import com.haulmont.cuba.gui.events.UiEvent;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

public class WidgetTreeElementClickedEvent extends DashboardEditEvent {

    private UUID layoutUuid;

    public WidgetTreeElementClickedEvent(UUID source) {
        super(source);
        layoutUuid = source;
    }

    @Override
    public UUID getSource() {
        return layoutUuid;
    }
}
