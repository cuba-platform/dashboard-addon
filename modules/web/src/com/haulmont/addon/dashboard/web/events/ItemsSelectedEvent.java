/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.events;

import com.haulmont.addon.dashboard.model.Widget;

import java.util.Collection;

public class ItemsSelectedEvent extends WidgetEvent {

    private Collection selected;

    public ItemsSelectedEvent(Widget source, Collection selected) {
        super(source);
        this.selected = selected;
    }

    public Collection getSelected() {
        return selected;
    }
}
