/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.model.visual_model;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

import java.util.ArrayList;
import java.util.List;

@MetaClass(name = "amxd$DashboardLayout")
public abstract class DashboardLayout extends BaseUuidEntity {
    @MetaProperty
    protected List<DashboardLayout> children = new ArrayList<>();

    /**
     * The expand ratio of given layout in a parent layout.
     */
    @MetaProperty
    protected Integer weight = 1;

    @MetaProperty
    protected String caption;

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public List<DashboardLayout> getChildren() {
        return children;
    }

    public void setChildren(List<DashboardLayout> children) {
        this.children = children;
    }

    public void addChild(DashboardLayout child) {
        children.add(child);
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
