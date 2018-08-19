/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.events;

import com.haulmont.addon.dashboard.model.dto.LayoutRemoveDto;
import com.haulmont.addon.dashboard.model.visual_model.DashboardLayout;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasLayout;
import com.haulmont.addon.dashboard.web.events.DashboardEvent;

public class LayoutChangedEvent extends DashboardEvent {

    public LayoutChangedEvent(LayoutRemoveDto source) {
        super(source);
    }

    @Override
    public LayoutRemoveDto getSource() {
        return (LayoutRemoveDto) super.getSource();
    }
}
