/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.annotation_analyzer;

import java.util.List;

public interface WidgetTypeAnalyzer {
    String NAME = "amxd_WidgetTypeAnalyzer";

    List<WidgetTypeInfo> getWidgetTypesInfo();
}
