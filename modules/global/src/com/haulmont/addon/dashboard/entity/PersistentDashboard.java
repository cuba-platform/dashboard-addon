/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.haulmont.addon.dashboard.entity;

import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Table(name = "DASHBOARD_PERSISTENT_DASHBOARD")
@Entity(name = "dashboard$PersistentDashboard")
public class PersistentDashboard extends StandardEntity {
    private static final long serialVersionUID = 3580405340289107373L;

    /**
     * Stores not persistence model {@link Dashboard} as JSON
     */
    @NotNull
    @Lob
    @Column(name = "DASHBOARD_MODEL", nullable = false)
    protected String dashboardModel;

    /**
     * The unique identifier for searching in a database. Duplicates with
     * {@link Dashboard#referenceName}
     */
    @NotNull
    @Column(name = "REFERENCE_NAME", nullable = false, unique = true)
    protected String referenceName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DASHBOARD_GROUP_ID")
    protected DashboardGroup dashboardGroup;

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