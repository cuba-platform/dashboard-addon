/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.Default;

@Source(type = SourceType.APP)
public interface WidgetConfig extends Config {

    @Property("amxd.dashboard.widgetsConfig")
    @Default("")
    String getWidgetConfigPaths();
    void setWidgetConfigPaths(String paths);
}