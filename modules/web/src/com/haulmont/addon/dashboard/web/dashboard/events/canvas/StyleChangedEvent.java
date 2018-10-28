/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.events.canvas;

import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasLayout;

public class StyleChangedEvent extends AbstractWidgetCanvasEvent {
    public StyleChangedEvent(CanvasLayout source) {
        super(source);
    }
}
