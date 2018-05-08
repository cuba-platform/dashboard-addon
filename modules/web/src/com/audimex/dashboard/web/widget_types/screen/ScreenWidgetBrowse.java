/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.widget_types.screen;

import com.audimex.dashboard.model.widget_types.ScreenWidget;
import com.audimex.dashboard.web.widget_types.AbstractWidgetBrowse;
import com.haulmont.cuba.gui.components.VBoxLayout;

import javax.inject.Inject;
import java.util.Map;

public class ScreenWidgetBrowse extends AbstractWidgetBrowse {
    @Inject
    protected VBoxLayout screenBox;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        String screenId = ((ScreenWidget) widget).getScreenId();
        openFrame(screenBox, screenId, getParamsForFrame());
    }
}
