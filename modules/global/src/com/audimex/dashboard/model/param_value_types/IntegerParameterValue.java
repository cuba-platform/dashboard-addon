/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.model.param_value_types;

public class IntegerParameterValue implements ParameterValue {
    protected Integer value;

    public IntegerParameterValue() {
    }

    public IntegerParameterValue(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("type: integer; value=%s", value);
    }
}
