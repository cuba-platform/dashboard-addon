/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.parameter.frames;

import com.haulmont.addon.dashboard.model.paramtypes.ParameterValue;

public interface ValueFrame {
    String VALUE_TYPE = "VALUE_TYPE";
    String VALUE = "VALUE";

    ParameterValue getValue();
}
