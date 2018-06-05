/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.events;

import com.audimex.dashboard.web.dashboard.layouts.CanvasWidgetLayout;
import com.haulmont.cuba.gui.events.UiEvent;
import org.springframework.context.ApplicationEvent;

public class OpenWidgetEditorEvent extends ApplicationEvent implements UiEvent {
    public OpenWidgetEditorEvent(CanvasWidgetLayout source) {
        super(source);
    }

    @Override
    public CanvasWidgetLayout getSource() {
        return (CanvasWidgetLayout) super.getSource();
    }
}
