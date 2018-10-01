/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.haulmont.addon.dashboard.entity;

import com.haulmont.cuba.core.entity.StandardEntity;

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