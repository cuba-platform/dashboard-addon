/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.haulmont.addon.dashboard.entity;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

@NamePattern("%s (%s)|name,code")
@Table(name = "DASHBOARD_PERSISTENT_DASHBOARD")
@Entity(name = "dashboard$PersistentDashboard")
public class PersistentDashboard extends StandardEntity {
    private static final long serialVersionUID = 3580405340289107373L;

    /**
     * Stores not persistence model {@link com.haulmont.addon.dashboard.model.Dashboard} as JSON
     */
    @NotNull
    @Lob
    @Column(name = "DASHBOARD_MODEL", nullable = false)
    protected String dashboardModel;

    /**
     * The unique identifier for searching in a database.
     */
    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;

    @NotNull
    @Column(name = "REFERENCE_NAME", nullable = false, unique = true)
    protected String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID")
    @OnDeleteInverse(DeletePolicy.DENY)
    protected DashboardGroup group;

    @Column(name = "IS_AVAILABLE_FOR_ALL_USERS")
    protected Boolean isAvailableForAllUsers = true;

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setGroup(DashboardGroup group) {
        this.group = group;
    }

    public DashboardGroup getGroup() {
        return group;
    }


    public void setIsAvailableForAllUsers(Boolean isAvailableForAllUsers) {
        this.isAvailableForAllUsers = isAvailableForAllUsers;
    }

    public Boolean getIsAvailableForAllUsers() {
        return isAvailableForAllUsers;
    }








    public void setDashboardModel(String dashboardModel) {
        this.dashboardModel = dashboardModel;
    }

    public String getDashboardModel() {
        return dashboardModel;
    }
}