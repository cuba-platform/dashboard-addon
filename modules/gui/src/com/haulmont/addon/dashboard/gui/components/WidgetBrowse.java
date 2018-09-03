/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.gui.components;

import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.cuba.gui.components.Frame;

import java.util.Map;

public interface WidgetBrowse extends Frame {
    Widget getWidget();

    Dashboard getDashboard();

    void refresh(Map<String, Object> params);
}
