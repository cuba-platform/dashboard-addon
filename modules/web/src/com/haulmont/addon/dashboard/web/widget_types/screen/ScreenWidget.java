/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.widget_types.screen;


import com.haulmont.addon.dashboard.gui.components.DashboardFrame;
import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.ParameterType;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.web.annotation.WidgetParam;
import com.haulmont.addon.dashboard.web.annotation.WidgetType;
import com.haulmont.addon.dashboard.web.annotation_analyzer.WidgetRepository;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractFrame;

import javax.inject.Inject;
import java.util.Map;

import static com.haulmont.addon.dashboard.web.widget_types.screen.ScreenWidget.CAPTION;

@WidgetType(name = CAPTION, editFrameId = "screenWidgetEdit")
public class ScreenWidget extends AbstractFrame {

    public static final String CAPTION = "Screen";

    @WidgetParam(type = ParameterType.STRING)
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
