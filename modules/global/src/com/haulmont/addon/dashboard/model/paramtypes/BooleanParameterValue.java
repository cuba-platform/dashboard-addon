/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.model.paramtypes;

public class BooleanParameterValue implements ParameterValue {
    protected Boolean value;

    public BooleanParameterValue() {
    }

    public BooleanParameterValue(Boolean value) {
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("type: boolean; value=%s", value);
    }
}