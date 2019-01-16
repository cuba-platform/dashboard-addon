/*
 * Copyright (c) 2008-2018 Haulmont.
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
 *
 */
package com.haulmont.addon.dashboard.model.visualmodel;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

import javax.validation.constraints.NotNull;

@MetaClass(name = "dashboard$ResponsiveArea")
public class ResponsiveArea extends BaseUuidEntity {
    private static final long serialVersionUID = 5378347946733397250L;

    @NotNull
    @MetaProperty(mandatory = true)
    protected DashboardLayout component;
    @NotNull
    @MetaProperty(mandatory = true)
    protected Integer order;

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public DashboardLayout getComponent() {
        return component;
    }

    public void setComponent(DashboardLayout component) {
        this.component = component;
    }
}
