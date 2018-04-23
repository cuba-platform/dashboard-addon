/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.model.param_types;

import java.util.List;

public class ListEntitiesParameter {
    protected List<EntityParameter> entityParameters;

    public List<EntityParameter> getEntityParameters() {
        return entityParameters;
    }

    public void setEntityParameters(List<EntityParameter> entityParameters) {
        this.entityParameters = entityParameters;
    }
}
