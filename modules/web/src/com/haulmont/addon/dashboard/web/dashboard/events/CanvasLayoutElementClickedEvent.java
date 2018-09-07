/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.events;

import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.events.UiEvent;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

public class CanvasLayoutElementClickedEvent extends ApplicationEvent implements UiEvent {

    private UUID layoutUuid;

    private Component.MouseEventDetails mouseEventDetails;

    public CanvasLayoutElementClickedEvent(UUID source, Component.MouseEventDetails mouseEventDetails) {
        super(source);
        layoutUuid = source;
        this.mouseEventDetails = mouseEventDetails;
    }

    @Override
    public UUID getSource() {
        return layoutUuid;
    }

    public Component.MouseEventDetails getMouseEventDetails() {
        return mouseEventDetails;
    }
}
