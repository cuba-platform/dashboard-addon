/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.events.model;

import com.haulmont.addon.dashboard.model.visualmodel.WidgetLayout;

public class WidgetEditEvent extends AbstractWidgetModelEvent {
    public WidgetEditEvent(WidgetLayout source) {
        super(source);
    }

    @Override
    public WidgetLayout getSource() {
        return (WidgetLayout) super.getSource();
    }
}
