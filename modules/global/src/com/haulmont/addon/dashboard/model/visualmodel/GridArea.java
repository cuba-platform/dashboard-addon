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
    protected Integer row1;
    @NotNull
    @MetaProperty(mandatory = true)
    protected Integer col1;

    public void setComponent(DashboardLayout component) {
        this.component = component;
    }

    public DashboardLayout getComponent() {
        return component;
    }

    public void setRow1(Integer row1) {
        this.row1 = row1;
    }

    public Integer getRow1() {
        return row1;
    }

    public void setCol1(Integer col1) {
        this.col1 = col1;
    }

    public Integer getCol1() {
        return col1;
    }
}
