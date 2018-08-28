/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.haulmont.addon.dashboard.web.dashboard.frames.browse;

import com.haulmont.addon.dashboard.web.dashboard.frames.ui_component.WebDashboardFrame;
import com.haulmont.cuba.gui.components.AbstractWindow;
import java.util.Map;

public class DashboardView extends AbstractWindow {

    public static final String SCREEN_NAME = "dashboard-view";
    public static final String REFERENCE_NAME = "REFERENCE_NAME";


    @Override
    public void init(Map<String, Object> params) {
        openFrame(this, WebDashboardFrame.SCREEN_NAME, params);
    }


}