/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.events.canvas;

import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasLayout;

public class WidgetRemovedEvent extends AbstractWidgetCanvasEvent {

    public WidgetRemovedEvent(CanvasLayout source) {
        super(source);
    }
}
