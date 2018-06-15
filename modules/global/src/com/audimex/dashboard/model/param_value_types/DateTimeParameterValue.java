/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.model.param_value_types;

import java.util.Date;

public class DateTimeParameterValue implements ParameterValue {
    protected Date value;

    public DateTimeParameterValue() {
    }

    public DateTimeParameterValue(Date value) {
        this.value = value;
    }

    public Date getValue() {
        return value;
    }

    public void setValue(Date value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("type: datetime; value=%s", value);
    }
}
