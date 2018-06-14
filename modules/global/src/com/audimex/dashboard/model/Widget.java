/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.model;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

import java.util.List;

@NamePattern("%s|caption")
@MetaClass(name = "amxd$Widget")
public class Widget extends BaseUuidEntity {
    @MetaProperty
    protected String widgetId;
    @MetaProperty
    protected String caption;
    @MetaProperty
    protected String description;
    @MetaProperty
    protected List<Parameter> parameters;
    @MetaProperty
    protected Boolean isAvailableForAllUsers = true;

    /**
     * Stores a login of the user, who created entity
     */
    @MetaProperty
    protected String createdBy;

    public Boolean getIsAvailableForAllUsers() {
        return isAvailableForAllUsers;
    }

    public void setIsAvailableForAllUsers(Boolean availableForAllUsers) {
        isAvailableForAllUsers = availableForAllUsers;
    }

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
}