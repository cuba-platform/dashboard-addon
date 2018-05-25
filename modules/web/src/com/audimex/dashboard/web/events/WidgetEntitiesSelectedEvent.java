/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.events;

import com.audimex.dashboard.model.Widget;
import com.haulmont.cuba.gui.events.UiEvent;
import org.springframework.context.ApplicationEvent;

import java.util.Collection;

public class WidgetEntitiesSelectedEvent extends ApplicationEvent implements UiEvent {
    public WidgetEntitiesSelectedEvent(WidgetWithEntities source) {
        super(source);
    }

    @Override
    public WidgetWithEntities getSource() {
        return (WidgetWithEntities) super.getSource();
    }

    public static class WidgetWithEntities {
        private Widget widget;
        private Collection entities;

        public WidgetWithEntities(Widget widget, Collection entities) {
            this.widget = widget;
            this.entities = entities;
        }

        public Widget getWidget() {
            return widget;
        }

        public Collection getEntities() {
            return entities;
        }
    }
}
