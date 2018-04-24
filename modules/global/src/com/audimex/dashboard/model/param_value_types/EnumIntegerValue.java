/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.model.param_value_types;

public class EnumIntegerValue implements Value {
    protected String enumerationClass;
    protected String enumIntegerValue;

    public String getEnumerationClass() {
        return enumerationClass;
    }

    public void setEnumerationClass(String enumerationClass) {
        this.enumerationClass = enumerationClass;
    }

    public String getEnumIntegerValue() {
        return enumIntegerValue;
    }

    public void setEnumIntegerValue(String enumIntegerValue) {
        this.enumIntegerValue = enumIntegerValue;
    }
}
