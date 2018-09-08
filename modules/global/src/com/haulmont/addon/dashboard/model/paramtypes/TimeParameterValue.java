/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.model.paramtypes;

import java.util.Date;

public class TimeParameterValue implements ParameterValue {
    protected Date value;

    public TimeParameterValue() {
    }

    public TimeParameterValue(Date value) {
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
        return String.format("type: time; value=%s", value);
    }
}
