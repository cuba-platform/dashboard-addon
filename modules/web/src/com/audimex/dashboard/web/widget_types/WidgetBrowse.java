/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.widget_types;

import com.audimex.dashboard.model.Dashboard;
import com.audimex.dashboard.model.Widget;
import com.haulmont.cuba.gui.components.Frame;

public interface WidgetBrowse extends Frame {
    Widget getWidget();

    Dashboard getDashboard();

    void refresh();
}
