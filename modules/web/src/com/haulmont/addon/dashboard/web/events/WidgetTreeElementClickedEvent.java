package com.haulmont.addon.dashboard.web.events;

import com.haulmont.cuba.gui.events.UiEvent;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

public class WidgetTreeElementClickedEvent extends ApplicationEvent implements UiEvent {

    private UUID layoutUuid;

    public WidgetTreeElementClickedEvent(UUID source) {
        super(source);
        layoutUuid = source;
    }

    @Override
    public UUID getSource() {
        return layoutUuid;
    }
}
