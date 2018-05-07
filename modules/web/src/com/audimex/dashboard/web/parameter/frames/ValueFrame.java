/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.parameter.frames;

import com.audimex.dashboard.model.param_value_types.ParameterValue;

public interface ValueFrame {
    String VALUE_TYPE = "VALUE_TYPE";
    String VALUE = "VALUE";

    ParameterValue getValue();
}
