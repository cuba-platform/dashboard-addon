/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.model.paramtypes;

public class StringParameterValue implements ParameterValue {
    protected String value;

    public StringParameterValue() {
    }

    public StringParameterValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("type: string; value=%s", value);
    }
}
