/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Lob;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "AMXD_DASHBOARD_PERSIST")
@Entity(name = "amxd$DashboardPersist")
public class DashboardPersist extends StandardEntity {
    private static final long serialVersionUID = 3580405340289107373L;

    @Lob
    @Column(name = "JSON_MODEL")
    protected String jsonModel;

    public void setJsonModel(String jsonModel) {
        this.jsonModel = jsonModel;
    }

    public String getJsonModel() {
        return jsonModel;
    }


}