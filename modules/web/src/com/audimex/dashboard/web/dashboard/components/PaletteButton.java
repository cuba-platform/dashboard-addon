/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.components;

import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.model.visual_model.LayoutType;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;

public class PaletteButton extends Button {

    protected LayoutType layoutType;
    protected Widget widget;

    public PaletteButton(String caption, Resource icon) {
        super(caption, icon);
        this.setWidth("100%");
        this.setHeight("50px");
    }

    public LayoutType getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(LayoutType layoutType) {
        this.layoutType = layoutType;
    }

    public Widget getWidget() {
        return widget;
    }

    public void setWidget(Widget widget) {
        this.widget = widget;
    }
}
