/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.visual_model;

public class GridArea extends DashboardLayout {
    protected DashboardLayout component;
    protected Integer column1;
    protected Integer row1;
    protected Integer column2;
    protected Integer row2;

    public GridArea() {
    }

    public GridArea(DashboardLayout component, Integer column1, Integer row1, Integer column2, Integer row2) {
        this.component = component;
        this.column1 = column1;
        this.row1 = row1;
        this.column2 = column2;
        this.row2 = row2;
    }

    public DashboardLayout getComponent() {
        return component;
    }

    public void setComponent(DashboardLayout component) {
        this.component = component;
    }

    public Integer getColumn1() {
        return column1;
    }

    public void setColumn1(Integer column1) {
        this.column1 = column1;
    }

    public Integer getRow1() {
        return row1;
    }

    public void setRow1(Integer row1) {
        this.row1 = row1;
    }

    public Integer getColumn2() {
        return column2;
    }

    public void setColumn2(Integer column2) {
        this.column2 = column2;
    }

    public Integer getRow2() {
        return row2;
    }

    public void setRow2(Integer row2) {
        this.row2 = row2;
    }
}
