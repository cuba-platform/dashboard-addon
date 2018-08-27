/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.widget_types.screen;


import com.haulmont.addon.dashboard.model.ParameterType;
import com.haulmont.addon.dashboard.web.annotation.WidgetParam;
import com.haulmont.addon.dashboard.web.annotation.WidgetType;
import com.haulmont.addon.dashboard.web.widget_types.AbstractWidgetBrowse;
import com.haulmont.cuba.gui.components.AbstractFrame;

import java.util.Map;

import static com.haulmont.addon.dashboard.web.widget_types.screen.ScreenWidgetBrowse.CAPTION;

@WidgetType(name = CAPTION, editFrameId = "screenWidgetEdit")
public class ScreenWidgetBrowse extends AbstractWidgetBrowse {

    public static final String CAPTION = "Screen";

    @WidgetParam(type = ParameterType.STRING)
    protected String screenId;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        refresh();
    }

    @Override
    public void refresh() {
        AbstractFrame screenFrame = openFrame(this, screenId, getParamsForFrame());
        screenFrame.setSizeFull();
    }

    @Override
    public void refresh(Map<String, Object> params) {
        AbstractFrame screenFrame = openFrame(this, screenId, getParamsForFrame(params));
        screenFrame.setSizeFull();
    }
}
