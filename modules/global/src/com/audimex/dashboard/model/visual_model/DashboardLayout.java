/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.model.visual_model;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

import java.util.ArrayList;
import java.util.List;

@MetaClass(name = "amxd$DashboardLayout")
public abstract class DashboardLayout extends BaseUuidEntity {
    @MetaProperty
    protected List<DashboardLayout> children = new ArrayList<>();

    public List<DashboardLayout> getChildren() {
        return children;
    }

    public void setChildren(List<DashboardLayout> children) {
        this.children = children;
    }
}
