/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.entity;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Table(name = "AMXD_DASHBOARD_PERSIST")
@Entity(name = "amxd$DashboardPersist")
public class DashboardPersist extends StandardEntity {
    private static final long serialVersionUID = 3580405340289107373L;

    @NotNull
    @Lob
    @Column(name = "DASHBOARD_MODEL", nullable = false)
    protected String dashboardModel;

    @NotNull
    @Column(name = "REFERENCE_NAME", nullable = false, unique = true)
    protected String referenceName;

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