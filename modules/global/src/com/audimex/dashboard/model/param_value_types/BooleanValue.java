/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.model.param_value_types;

public class BooleanValue implements Value {
    protected Boolean value;

    public BooleanValue() {
    }

    public BooleanValue(Boolean value) {
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }
}
