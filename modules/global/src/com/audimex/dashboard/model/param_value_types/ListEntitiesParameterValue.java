/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.model.param_value_types;

import java.util.ArrayList;
import java.util.List;

public class ListEntitiesParameterValue implements ParameterValue {
    protected List<EntityParameterValue> entityValues;

    public ListEntitiesParameterValue() {
        entityValues = new ArrayList<>();
    }

    public ListEntitiesParameterValue(List<EntityParameterValue> entityValues) {
        this.entityValues = entityValues;
    }

    public List<EntityParameterValue> getValue() {
        return entityValues;
    }

    public void setValue(List<EntityParameterValue> entityValues) {
        this.entityValues = entityValues;
    }

    @Override
    public String toString() {
        return "type: list entities";
    }
}
