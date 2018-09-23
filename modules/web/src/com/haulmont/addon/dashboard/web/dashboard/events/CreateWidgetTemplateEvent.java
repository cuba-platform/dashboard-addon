/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.events;

import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.cuba.gui.events.UiEvent;
import org.springframework.context.ApplicationEvent;

public class CreateWidgetTemplateEvent extends ApplicationEvent implements UiEvent {

    public CreateWidgetTemplateEvent(Widget source) {
        super(source);
    }

    @Override
    public Widget getSource() {
        return (Widget) super.getSource();
    }
}
