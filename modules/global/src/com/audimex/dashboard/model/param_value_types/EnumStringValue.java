/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.model.param_value_types;

public class EnumStringValue implements Value {
    protected String enumerationClass;
    protected String enumStringValue;

    public String getEnumerationClass() {
        return enumerationClass;
    }

    public void setEnumerationClass(String enumerationClass) {
        this.enumerationClass = enumerationClass;
    }

    public String getEnumStringValue() {
        return enumStringValue;
    }

    public void setEnumStringValue(String enumStringValue) {
        this.enumStringValue = enumStringValue;
    }
}
