/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.addon.dashboard.model.paramtypes;

import java.util.HashMap;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityParameterValue that = (EntityParameterValue) o;
        return Objects.equals(metaClassName, that.metaClassName) &&
                Objects.equals(entityId, that.entityId) &&
                Objects.equals(viewName, that.viewName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(metaClassName, entityId, viewName);
    }
}
