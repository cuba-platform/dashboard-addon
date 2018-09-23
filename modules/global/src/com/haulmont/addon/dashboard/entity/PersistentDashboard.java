/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.haulmont.addon.dashboard.entity;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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
     * The unique identifier for searching in a database. Duplicates with
     * {@link com.haulmont.addon.dashboard.model.Dashboard#referenceName}
     */
    @NotNull
    @Column(name = "REFERENCE_NAME", nullable = false, unique = true)
    protected String referenceName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DASHBOARD_GROUP_ID")
    protected DashboardGroup dashboardGroup;

    @Column(name = "IS_AVAILABLE_FOR_ALL_USERS")
    protected Boolean isAvailableForAllUsers = true;

    public void setIsAvailableForAllUsers(Boolean isAvailableForAllUsers) {
        this.isAvailableForAllUsers = isAvailableForAllUsers;
    }

    public Boolean getIsAvailableForAllUsers() {
        return isAvailableForAllUsers;
    }


    public void setDashboardGroup(DashboardGroup dashboardGroup) {
        this.dashboardGroup = dashboardGroup;
    }

    public DashboardGroup getDashboardGroup() {
        return dashboardGroup;
    }


    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public String getReferenceName() {
        return referenceName;
    }


    public void setDashboardModel(String dashboardModel) {
        this.dashboardModel = dashboardModel;
    }

    public String getDashboardModel() {
        return dashboardModel;
    }
}