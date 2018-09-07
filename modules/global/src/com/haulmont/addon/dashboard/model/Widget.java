/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
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
@MetaClass(name = "amxd$Widget")
public class Widget extends BaseUuidEntity {
    @MetaProperty
    protected String widgetId;
    @MetaProperty
    protected String caption;
    @MetaProperty
    protected String description;
    @MetaProperty
    protected List<Parameter> parameters = new ArrayList<>();
    @MetaProperty
    protected Boolean isAvailableForAllUsers = true;
    @MetaProperty
    protected List<Parameter> widgetFields = new ArrayList<>();
    @MetaProperty
    protected String browseFrameId;
    @MetaProperty
    @Exclude
    protected Dashboard dashboard;

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

    public List<Parameter> getWidgetFields() {
        return widgetFields;
    }

    public void setWidgetFields(List<Parameter> widgetFields) {
        this.widgetFields = widgetFields;
    }

    public String getBrowseFrameId() {
        return browseFrameId;
    }

    public void setBrowseFrameId(String browseFrameId) {
        this.browseFrameId = browseFrameId;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }
}