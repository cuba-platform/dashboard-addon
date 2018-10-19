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
package com.haulmont.addon.dashboard.entity;

import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Persistence Widget Template. The field {@link WidgetTemplate#widgetModel} stores not persistence model
 * {@link com.haulmont.addon.dashboard.model.Widget} as JSON
 */
@Table(name = "DASHBOARD_WIDGET_TEMPLATE")
@Entity(name = "dashboard$WidgetTemplate")
public class WidgetTemplate extends StandardEntity {
    private static final long serialVersionUID = -2121278399449430493L;

    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;

    @Column(name = "IS_AVAILABLE_FOR_ALL_USERS")
    protected Boolean isAvailableForAllUsers = true;

    @NotNull
    @Lob
    @Column(name = "WIDGET_MODEL", nullable = false)
    protected String widgetModel;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID")
    @OnDeleteInverse(DeletePolicy.DENY)
    protected WidgetTemplateGroup group;

    public void setGroup(WidgetTemplateGroup group) {
        this.group = group;
    }

    public WidgetTemplateGroup getGroup() {
        return group;
    }


    public void setIsAvailableForAllUsers(Boolean isAvailableForAllUsers) {
        this.isAvailableForAllUsers = isAvailableForAllUsers;
    }

    public Boolean getIsAvailableForAllUsers() {
        return isAvailableForAllUsers;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }



    public void setWidgetModel(String widgetModel) {
        this.widgetModel = widgetModel;
    }

    public String getWidgetModel() {
        return widgetModel;
    }


}