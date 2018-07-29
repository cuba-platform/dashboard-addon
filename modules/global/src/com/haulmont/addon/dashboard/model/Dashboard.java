/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.haulmont.addon.dashboard.model;

import com.haulmont.addon.dashboard.entity.PersistentDashboard;
import com.haulmont.addon.dashboard.model.visual_model.VerticalLayout;
import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

import javax.validation.constraints.NotNull;
import java.util.List;

@NamePattern("%s|title")
@MetaClass(name = "amxd$Dashboard")
public class Dashboard extends BaseUuidEntity {
    @NotNull
    @MetaProperty(mandatory = true)
    protected String title;

    /**
     * The unique identifier for searching in a database. Duplicates with
     * {@link PersistentDashboard#referenceName}
     */
    @NotNull
    @MetaProperty(mandatory = true)
    protected String referenceName;

    /**
     * Stores a hierarchy of a visual model
     */
    @MetaProperty
    protected VerticalLayout visualModel;
    @MetaProperty
    protected List<Parameter> parameters;
    @MetaProperty
    protected Boolean isAvailableForAllUsers = true;

    /**
     * Stores a login of the user, who created entity
     */
    @MetaProperty
    protected String createdBy;

    /**
     * Stores delay for publishing DashboardUpdatedEvent in view mode
     */
    @MetaProperty
    protected Integer timerDelay = 0;

    public Boolean getIsAvailableForAllUsers() {
        return isAvailableForAllUsers;
    }

    public void setIsAvailableForAllUsers(Boolean availableForAllUsers) {
        isAvailableForAllUsers = availableForAllUsers;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public VerticalLayout getVisualModel() {
        return visualModel;
    }

    public void setVisualModel(VerticalLayout visualModel) {
        this.visualModel = visualModel;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public Integer getTimerDelay() {
        return timerDelay;
    }

    public void setTimerDelay(Integer timerDelay) {
        this.timerDelay = timerDelay;
    }
}