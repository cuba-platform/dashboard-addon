/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.model.param_value_types;

import java.io.Serializable;

public interface ParameterValue extends Serializable {
    Object getValue();
}
