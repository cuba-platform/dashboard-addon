/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.dashboard.frames.browse;

import com.audimex.dashboard.web.dashboard.frames.ui_component.WebDashboardFrame;
import com.haulmont.cuba.gui.components.AbstractWindow;

import java.util.Map;

public class DashboardView extends AbstractWindow {

    public static final String SCREEN_NAME = "dashboard-view";
    public static final String REFERENCE_NAME = "REFERENCE_NAME";

    @Override
    public void init(Map<String, Object> params) {
        WebDashboardFrame frame = (WebDashboardFrame) openFrame(null, WebDashboardFrame.SCREEN_NAME);
        frame.setReferenceName((String) params.getOrDefault(REFERENCE_NAME, ""));
        this.add(frame);
        //frame.refresh();
    }
}