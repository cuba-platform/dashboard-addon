/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.model.param_value_types;

import java.math.BigDecimal;

public class DecimalParameterValue implements ParameterValue {
    BigDecimal value;

    public DecimalParameterValue() {
    }

    public DecimalParameterValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("type: decimal; value=%s", value);
    }
}
