/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.model.paramtypes;

public class EnumParameterValue implements ParameterValue {
    protected String enumClassName;

    public EnumParameterValue() {
    }

    public EnumParameterValue(String enumClassName) {
        this.enumClassName = enumClassName;
    }

    public String getValue() {
        return enumClassName;
    }

    public void setValue(String enumClassName) {
        this.enumClassName = enumClassName;
    }

    @Override
    public String toString() {
        return String.format("type: enum; enumClassName=%s", enumClassName);
    }
}
