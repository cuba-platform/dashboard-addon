/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.annotation_analyzer;

import com.audimex.dashboard.annotation.WidgetType;

import java.util.List;

/**
 * Scanning the project for use classes with the annotation {@link WidgetType} and
 * provides information {@link WidgetTypeInfo} about these classes
 */
public interface WidgetTypeAnalyzer {
    String NAME = "amxd_WidgetTypeAnalyzer";

    List<WidgetTypeInfo> getWidgetTypesInfo();
}
