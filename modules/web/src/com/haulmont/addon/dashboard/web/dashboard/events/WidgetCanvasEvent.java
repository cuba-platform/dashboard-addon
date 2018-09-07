/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.events;

import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasLayout;
import com.haulmont.cuba.gui.events.UiEvent;
import org.springframework.context.ApplicationEvent;

public class WidgetCanvasEvent extends ApplicationEvent implements UiEvent {

    public WidgetCanvasEvent(CanvasLayout source) {
        super(source);
    }

    @Override
    public CanvasLayout getSource() {
        return (CanvasLayout) super.getSource();
    }
}
