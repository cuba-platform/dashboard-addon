/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.events;

import com.haulmont.addon.dashboard.web.dto.LayoutRemoveDto;

public class WidgetRemovedEvent extends DashboardEditEvent {

    public WidgetRemovedEvent(LayoutRemoveDto source) {
        super(source);
    }

    @Override
    public LayoutRemoveDto getSource() {
        return (LayoutRemoveDto) super.getSource();
    }
}
