/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.events;

import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasWidgetLayout;

public class OpenWidgetEditorEvent extends WidgetCanvasEvent {
    public OpenWidgetEditorEvent(CanvasWidgetLayout source) {
        super(source);
    }

    @Override
    public CanvasWidgetLayout getSource() {
        return (CanvasWidgetLayout) super.getSource();
    }
}
