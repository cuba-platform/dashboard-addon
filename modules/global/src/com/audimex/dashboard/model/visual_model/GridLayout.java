/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.model.visual_model;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@MetaClass(name = "amxd$GridLayout")
public class GridLayout extends DashboardLayout {
    @MetaProperty
    protected Integer rows = 0;
    @MetaProperty
    protected Integer columns = 0;
    @MetaProperty
    protected Set<GridArea> areas = new HashSet<>();

    public void setAreas(Set<GridArea> areas) {
        this.areas = areas;
    }

    public Set<GridArea> getAreas() {
        return areas;
    }

    public void addArea(GridArea area) {
        areas.add(area);
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getColumns() {
        return columns;
    }

    public void setColumns(Integer columns) {
        this.columns = columns;
    }

    @Override
    public List<DashboardLayout> getChildren() {
        throw new UnsupportedOperationException("Use getArea() method");
    }

    @Override
    public void setChildren(List<DashboardLayout> children) {
        throw new UnsupportedOperationException("Use setArea() method");
    }

    @Override
    public void addChild(DashboardLayout child) {
        throw new UnsupportedOperationException("Use addArea() method");
    }
}
