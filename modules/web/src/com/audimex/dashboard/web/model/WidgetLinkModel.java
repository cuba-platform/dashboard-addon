/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * @author bochkarev
 * @version $Id$
 */
public class WidgetLinkModel implements Serializable {

    private static final long serialVersionUID = -758643230107290253L;

    protected UUID dashboard;
    protected String filter;
    protected List<WidgetParameterModel> dashboardParameters;

    public UUID getDashboard() {
        return dashboard;
    }

    public void setDashboard(UUID dashboard) {
        this.dashboard = dashboard;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public List<WidgetParameterModel> getDashboardParameters() {
        return dashboardParameters;
    }

    public void setDashboardParameters(List<WidgetParameterModel> dashboardParameters) {
        this.dashboardParameters = dashboardParameters;
    }
}
