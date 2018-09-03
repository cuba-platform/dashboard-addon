/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.annotation_analyzer;


import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.cuba.gui.components.AbstractFrame;

import java.util.List;
import java.util.Map;

/**
 * Scanning the project for use classes with the annotation {@link WidgetType} and
 * provides information {@link WidgetTypeInfo} about these classes
 */
public interface WidgetRepository {
    String NAME = "amxd_WidgetRepository";

    List<WidgetTypeInfo> getWidgetTypesInfo();

    void initializeWidgetFields(AbstractFrame widgetFrame, Widget widget);

    void serializeWidgetFields(AbstractFrame widgetFrame, Widget widget);

    Map<String, Object> getWidgetParams(Widget widget);
}
