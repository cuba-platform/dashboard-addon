/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.visual_model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DashboardVisualModel implements Serializable {
    protected String title;
    protected List<DashboardLayout> layouts = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<DashboardLayout> getLayouts() {
        return layouts;
    }

    public void setLayouts(List<DashboardLayout> layouts) {
        this.layouts = layouts;
    }
}
