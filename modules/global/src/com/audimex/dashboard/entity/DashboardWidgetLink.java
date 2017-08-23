/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "AMXD_DASHBOARD_WIDGET_LINK")
@Entity(name = "amxd$DashboardWidgetLink")
public class DashboardWidgetLink extends StandardEntity {
    private static final long serialVersionUID = -4430630343799535160L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DASHBOARD_ID")
    protected Dashboard dashboard;

    @Column(name = "FILTER_")
    protected String filter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DASHBOARD_WIDGET_ID")
    protected DashboardWidget dashboardWidget;

    public void setDashboardWidget(DashboardWidget dashboardWidget) {
        this.dashboardWidget = dashboardWidget;
    }

    public DashboardWidget getDashboardWidget() {
        return dashboardWidget;
    }


    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getFilter() {
        return filter;
    }


}