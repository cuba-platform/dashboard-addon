/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard;

import com.audimex.dashboard.entity.Dashboard;
import com.audimex.dashboard.entity.DashboardWidget;
import com.audimex.dashboard.entity.WidgetType;
import com.audimex.dashboard.web.dashboard.events.DashboardEvent;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;

import java.util.function.Consumer;

public class PaletteButton extends Button {

    private WidgetType widgetType;
    private DashboardWidget widget;
    private Dashboard dashboard;
    private Consumer<DashboardEvent> dashboardEventExecutor;

    public PaletteButton(String caption, Resource icon) {
        super(caption, icon);
    }

    public WidgetType getWidgetType() {
        return widgetType;
    }

    public void setWidgetType(WidgetType widgetType) {
        this.widgetType = widgetType;
    }

    public DashboardWidget getWidget() {
        return widget;
    }

    public void setWidget(DashboardWidget widget) {
        this.widget = widget;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public Consumer<DashboardEvent> getDashboardEventExecutor() {
        return dashboardEventExecutor;
    }

    public void setDashboardEventExecutor(Consumer<DashboardEvent> dashboardEventExecutor) {
        this.dashboardEventExecutor = dashboardEventExecutor;
    }
}