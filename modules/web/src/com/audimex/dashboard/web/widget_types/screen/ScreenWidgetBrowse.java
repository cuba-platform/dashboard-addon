/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.widget_types.screen;

import com.audimex.dashboard.model.widget_types.ScreenWidget;
import com.audimex.dashboard.web.widget_types.AbstractWidgetBrowse;
import com.haulmont.cuba.gui.components.AbstractFrame;

import java.util.Map;

public class ScreenWidgetBrowse extends AbstractWidgetBrowse {

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        refresh();
    }

    @Override
    public void refresh() {
        String screenId = ((ScreenWidget) widget).getScreenId();
        AbstractFrame screenFrame = openFrame(this, screenId, getParamsForFrame());
        screenFrame.setSizeFull();
    }

    @Override
    public void refresh(Map<String, Object> params) {
        String screenId = ((ScreenWidget) widget).getScreenId();
        AbstractFrame screenFrame = openFrame(this, screenId, getParamsForFrame(params));
        screenFrame.setSizeFull();
    }
}
