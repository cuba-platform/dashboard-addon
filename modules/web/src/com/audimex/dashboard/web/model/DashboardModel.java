/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DashboardModel implements Serializable {
    protected String title;
    protected List<WidgetModel> widgets = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<WidgetModel> getWidgets() {
        return widgets;
    }

    public void setWidgets(List<WidgetModel> widgets) {
        this.widgets = widgets;
    }
}
