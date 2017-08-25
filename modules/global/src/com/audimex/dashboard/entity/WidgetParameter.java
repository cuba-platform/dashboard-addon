/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.entity;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;

@Table(name = "AMXD_WIDGET_PARAMETER")
@Entity(name = "amxd$WidgetParameter")
public class WidgetParameter extends StandardEntity {
    private static final long serialVersionUID = -6936139215635675937L;

    @Column(name = "NAME")
    protected String name;

    @Column(name = "PARAMETER_TYPE")
    protected Integer parameterType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DASHBOARD_WIDGET_ID")
    protected DashboardWidget dashboardWidget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DASHBOARD_WIDGET_LINK_ID")
    protected DashboardWidgetLink dashboardWidgetLink;

    public void setDashboardWidgetLink(DashboardWidgetLink dashboardWidgetLink) {
        this.dashboardWidgetLink = dashboardWidgetLink;
    }

    public DashboardWidgetLink getDashboardWidgetLink() {
        return dashboardWidgetLink;
    }


    public void setDashboardWidget(DashboardWidget dashboardWidget) {
        this.dashboardWidget = dashboardWidget;
    }

    public DashboardWidget getDashboardWidget() {
        return dashboardWidget;
    }


    public void setParameterType(WidgetParameterType parameterType) {
        this.parameterType = parameterType == null ? null : parameterType.getId();
    }

    public WidgetParameterType getParameterType() {
        return parameterType == null ? null : WidgetParameterType.fromId(parameterType);
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}