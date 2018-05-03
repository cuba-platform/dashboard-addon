/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.config;

import com.audimex.dashboard.config.widget_types_config.WidgetTypesConverter;
import com.audimex.dashboard.config.widget_types_config.WidgetTypesInfo;
import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.Default;
import com.haulmont.cuba.core.config.type.Factory;
import com.haulmont.cuba.core.config.type.Stringify;

@Source(type = SourceType.DATABASE)
public interface WidgetTypesConfig extends Config {

    @Property("amxd.dashboard.widgetTypes")
    @Factory(factory = WidgetTypesConverter.Factory.class)
    @Stringify(stringify = WidgetTypesConverter.Stringify.class)
    @Default("{}")
    WidgetTypesInfo getWidgetTypesInfo();

    void setWidgetTypesInfo(WidgetTypesInfo widgetTypesInfo);
}
