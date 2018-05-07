/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.model.param_value_types;

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
}
