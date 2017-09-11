/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web;

import com.audimex.dashboard.entity.DashboardWidget;

import java.util.List;

public interface WidgetRepository {
    String NAME = "amxd_WidgetRepository";

    List<DashboardWidget> getWidgets();
}