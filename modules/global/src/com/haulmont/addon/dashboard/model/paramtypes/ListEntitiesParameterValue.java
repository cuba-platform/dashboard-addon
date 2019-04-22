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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListEntitiesParameterValue that = (ListEntitiesParameterValue) o;
        if ((entityValues == null & that.entityValues != null) || (entityValues != null & that.entityValues == null)) {
            return false;
        }

        if (entityValues != null && that.entityValues != null) {
            if (entityValues.size() != that.entityValues.size()) {
                return false;
            }

            for (EntityParameterValue epv : entityValues) {
                Optional<EntityParameterValue> epvOpt = that.entityValues.stream().filter(v -> v.equals(epv)).findFirst();
                if (!epvOpt.isPresent()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {

        return Objects.hash(entityValues);
    }
}
