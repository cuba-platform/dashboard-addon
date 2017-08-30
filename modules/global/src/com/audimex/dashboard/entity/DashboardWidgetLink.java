/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.entity;

import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "dashboardWidgetLink", cascade = CascadeType.ALL)
    protected List<WidgetParameter> dashboardParameters;

    public void setDashboardParameters(List<WidgetParameter> dashboardParameters) {
        this.dashboardParameters = dashboardParameters;
    }

    public void addDashboardParameter(WidgetParameter widgetParameter) {
        if (dashboardParameters == null) {
            dashboardParameters = new ArrayList<>();
        }
        dashboardParameters.add(widgetParameter);
    }

    public List<WidgetParameter> getDashboardParameters() {
        return dashboardParameters;
    }


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