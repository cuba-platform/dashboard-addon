/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.widget.screen;


import com.haulmont.addon.dashboard.gui.components.DashboardFrame;
import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.web.annotation.DashboardWidget;
import com.haulmont.addon.dashboard.web.annotation.WidgetParam;
import com.haulmont.addon.dashboard.web.repository.WidgetRepository;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractFrame;

import javax.inject.Inject;
import java.util.Map;

import static com.haulmont.addon.dashboard.web.widget.screen.ScreenWidget.CAPTION;

@DashboardWidget(name = CAPTION, editFrameId = "dashboard$ScreenWidgetEdit")
public class ScreenWidget extends AbstractFrame {

    public static final String CAPTION = "Screen";

    @WidgetParam
    @WindowParam
    protected String screenId;

    @Inject
    protected WidgetRepository widgetRepository;

    @WindowParam
    protected Widget widget;

    @WindowParam
    protected Dashboard dashboard;

    @WindowParam
    protected DashboardFrame dashboardFrame;

    protected AbstractFrame screenFrame;

    @Override
    public void init(Map<String, Object> params) {
        screenFrame = openFrame(this, screenId, widgetRepository.getWidgetParams(widget));
        screenFrame.setSizeFull();
    }
}
