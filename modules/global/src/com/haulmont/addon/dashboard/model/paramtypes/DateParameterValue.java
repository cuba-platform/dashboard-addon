/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.model.paramtypes;

import java.util.Date;

public class DateParameterValue implements ParameterValue {
    protected Date value;

    public DateParameterValue() {
    }

    public DateParameterValue(Date value) {
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
        return String.format("type: date; value=%s", value);
    }
}
