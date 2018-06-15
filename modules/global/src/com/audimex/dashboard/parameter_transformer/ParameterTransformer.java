/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.parameter_transformer;

import com.audimex.dashboard.model.param_value_types.ParameterValue;

public interface ParameterTransformer {
    String NAME = "amxd_ParameterTransformer";

    Object transform(ParameterValue parameterValue);
}
