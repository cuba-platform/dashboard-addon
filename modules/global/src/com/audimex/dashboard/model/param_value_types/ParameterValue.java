/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.model.param_value_types;

import java.io.Serializable;

public interface ParameterValue extends Serializable {
    Object getValue();
}
