/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.haulmont.addon.dashboard.web.dashboard.frames.view;

import com.haulmont.addon.dashboard.web.dashboard.frames.uicomponent.WebDashboardFrame;
import com.haulmont.cuba.gui.components.AbstractWindow;

import java.util.Map;

public class DashboardView extends AbstractWindow {

    public static final String SCREEN_NAME = "dashboard$DashboardView";
    public static final String CODE = "CODE";
    public static final String DISPLAY_NAME = "DISPLAY_NAME";

    @Override
    public void init(Map<String, Object> params) {
        if (params.containsKey(DISPLAY_NAME)){
            setCaption((String) params.get(DISPLAY_NAME));
        }
        openFrame(this, WebDashboardFrame.SCREEN_NAME, params);
    }


}