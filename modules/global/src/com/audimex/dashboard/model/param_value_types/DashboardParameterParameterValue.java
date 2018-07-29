package com.audimex.dashboard.model.param_value_types;

import com.audimex.dashboard.model.Parameter;

public class DashboardParameterParameterValue implements ParameterValue {
    protected Parameter value;

    public DashboardParameterParameterValue() {
    }

    public DashboardParameterParameterValue(Parameter value) {
        this.value = value;
    }

    @Override
    public Parameter getValue() {
        return value;
    }

    public void setValue(Parameter value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("type: Parameter; value=%s", value);
    }
}
