/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.model.param_value_types;

import java.util.HashMap;

public class EntityParameterValue implements ParameterValue {
    protected String metaClassName;
    protected String entityId;
    protected String viewName;

    public EntityParameterValue() {
    }

    public EntityParameterValue(String metaClassName, String entityId, String viewName) {
        this.metaClassName = metaClassName;
        this.entityId = entityId;
        this.viewName = viewName;
    }

    @Override
    public Object getValue() {
        return new HashMap<String, String>() {
            {
                put("metaClassName", metaClassName);
                put("entityId", entityId);
                put("viewName", viewName);
            }
        };
    }

    public String getMetaClassName() {
        return metaClassName;
    }

    public void setMetaClassName(String metaClassName) {
        this.metaClassName = metaClassName;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    @Override
    public String toString() {
        return String.format("type: entity; metaClassName=%s, entityId=%s, viewName=%s", metaClassName, entityId, viewName);
    }
}
