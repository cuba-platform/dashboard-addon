/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.model.paramtypes;

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
