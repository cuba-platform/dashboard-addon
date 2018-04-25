/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.model.param_value_types;

public class IntegerValue implements Value {
    protected Integer value;

    public IntegerValue() {
    }

    public IntegerValue(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
