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

import java.util.Objects;

public class EnumParameterValue implements ParameterValue {
    protected String enumClassName;

    public EnumParameterValue() {
    }

    public EnumParameterValue(String enumClassName) {
        this.enumClassName = enumClassName;
    }

    public String getValue() {
        return enumClassName;
    }

    public void setValue(String enumClassName) {
        this.enumClassName = enumClassName;
    }

    @Override
    public String toString() {
        return String.format("type: enum; enumClassName=%s", enumClassName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnumParameterValue that = (EnumParameterValue) o;
        return Objects.equals(enumClassName, that.enumClassName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(enumClassName);
    }
}
