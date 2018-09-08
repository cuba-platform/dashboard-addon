/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.parametertransformer;

import com.haulmont.addon.dashboard.model.ParameterType;
import com.haulmont.addon.dashboard.model.paramtypes.ParameterValue;
import com.haulmont.cuba.gui.components.AbstractFrame;

import java.lang.reflect.Field;

public interface ParameterTransformer {
    String NAME = "dashboard_ParameterTransformer";

    Object transform(ParameterValue parameterValue);

    boolean compareParameterTypes(ParameterType parameterType, Field field);

    ParameterValue createParameterValue(ParameterType parameterType, Field field, AbstractFrame widgetFrame);
}
