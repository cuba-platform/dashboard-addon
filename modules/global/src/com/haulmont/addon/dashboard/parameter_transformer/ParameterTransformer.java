/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.parameter_transformer;

import com.haulmont.addon.dashboard.model.param_value_types.ParameterValue;

public interface ParameterTransformer {
    String NAME = "amxd_ParameterTransformer";

    Object transform(ParameterValue parameterValue);
}
