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
package com.haulmont.addon.dashboard.model;

import com.haulmont.addon.dashboard.model.json.Exclude;
import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

import java.util.ArrayList;
import java.util.List;

@NamePattern("%s|caption")
@MetaClass(name = "dashboard$Widget")
public class Widget extends BaseUuidEntity {
    @MetaProperty
    protected Boolean showWidgetCaption = false;
    @MetaProperty
    protected String widgetId;
    @MetaProperty
    protected String caption;
    @MetaProperty
    protected String name;
    @MetaProperty
    protected String description;
    @MetaProperty
    protected List<Parameter> parameters = new ArrayList<>();
    @MetaProperty
    protected List<Parameter> widgetFields = new ArrayList<>();
    @MetaProperty
    protected String frameId;
    @MetaProperty
    @Exclude
    protected Dashboard dashboard;

    /**
     * Stores a login of the user, who created entity
     */
    @MetaProperty
    protected String createdBy;

    public String getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(String widgetId) {
        this.widgetId = widgetId;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public List<Parameter> getWidgetFields() {
        return widgetFields;
    }

    public void setWidgetFields(List<Parameter> widgetFields) {
        this.widgetFields = widgetFields;
    }

    public String getFrameId() {
        return frameId;
    }

    public void setFrameId(String frameId) {
        this.frameId = frameId;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getShowWidgetCaption() {
        return showWidgetCaption;
    }

    public void setShowWidgetCaption(Boolean showWidgetCaption) {
        this.showWidgetCaption = showWidgetCaption;
    }
}