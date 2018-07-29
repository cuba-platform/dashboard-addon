/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.model.param_value_types;

public class LongParameterValue implements ParameterValue {
    protected Long value;

    public LongParameterValue() {
    }

    public LongParameterValue(Long value) {
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("type: long; value=%s", value);
    }
}
