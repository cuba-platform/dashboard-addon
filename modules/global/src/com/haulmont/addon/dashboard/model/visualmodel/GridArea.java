/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.haulmont.addon.dashboard.model.visualmodel;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;

import javax.validation.constraints.NotNull;

import com.haulmont.cuba.core.entity.BaseUuidEntity;

@MetaClass(name = "dashboard$GridArea")
public class GridArea extends BaseUuidEntity {
    private static final long serialVersionUID = 5378347946733397250L;

    @NotNull
    @MetaProperty(mandatory = true)
    protected DashboardLayout component;
    @NotNull
    @MetaProperty(mandatory = true)
    protected Integer row;
    @NotNull
    @MetaProperty(mandatory = true)
    protected Integer col;

    public void setComponent(DashboardLayout component) {
        this.component = component;
    }

    public DashboardLayout getComponent() {
        return component;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getRow() {
        return row;
    }

    public void setCol(Integer col) {
        this.col = col;
    }

    public Integer getCol() {
        return col;
    }
}
