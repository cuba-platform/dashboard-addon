/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.events.widget;

import com.haulmont.addon.dashboard.model.Widget;

public class WidgetEditEvent extends AbstractWidgetEvent {
    public WidgetEditEvent(Widget source) {
        super(source);
    }
}
