/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.palette;

import com.audimex.dashboard.entity.WidgetType;
import com.audimex.dashboard.web.repo.WidgetRepository;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;

public class PaletteButton extends Button {

    private WidgetType widgetType;
    private WidgetRepository.Widget widget;

    public PaletteButton(String caption) {
        super(caption);
    }

    public PaletteButton(String caption, Resource icon) {
        super(caption, icon);
    }

    public WidgetType getWidgetType() {
        return widgetType;
    }

    public void setWidgetType(WidgetType widgetType) {
        this.widgetType = widgetType;
    }

    public WidgetRepository.Widget getWidget() {
        return widget;
    }

    public void setWidget(WidgetRepository.Widget widget) {
        this.widget = widget;
    }
}