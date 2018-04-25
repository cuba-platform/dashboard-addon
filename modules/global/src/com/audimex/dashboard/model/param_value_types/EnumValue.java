/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.model.param_value_types;

public class EnumValue implements Value {
    protected String enumClassName;

    public EnumValue() {
    }

    public EnumValue(String enumClassName) {
        this.enumClassName = enumClassName;
    }

    public String getEnumClassName() {
        return enumClassName;
    }

    public void setEnumClassName(String enumClassName) {
        this.enumClassName = enumClassName;
    }
}
