/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.events.canvas;

import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasLayout;
import com.haulmont.cuba.gui.events.UiEvent;
import org.springframework.context.ApplicationEvent;

public abstract class AbstractWidgetCanvasEvent extends ApplicationEvent implements UiEvent {

    public AbstractWidgetCanvasEvent(CanvasLayout source) {
        super(source);
    }

    @Override
    public CanvasLayout getSource() {
        return (CanvasLayout) super.getSource();
    }
}
