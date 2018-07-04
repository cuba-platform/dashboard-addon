/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.gui.components;

import com.audimex.dashboard.model.Dashboard;
import com.audimex.dashboard.model.Parameter;
import com.haulmont.cuba.gui.components.Frame;

import java.util.List;

/**
 * Represents in a frame  a dashboard {@link Dashboard}
 * Dashboard can be configured by {@link Dashboard#referenceName} or by the classpath of dashboard.json
 * It is possible to add (or rewrite) primitive parameters in the dashboard.
 */
public interface DashboardFrame extends Frame {

    String NAME = "amdxDashboardComponent";

    WidgetBrowse getWidgetBrowse(String widgetId);

    void setReferenceName(String referenceName);

    void setJsonPath(String jsonPath);

    void setXmlParameters(List<Parameter> parameters);
}
