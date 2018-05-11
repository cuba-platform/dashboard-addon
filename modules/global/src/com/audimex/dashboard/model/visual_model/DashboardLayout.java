/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.model.visual_model;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

import java.util.LinkedList;

@MetaClass(name = "amxd$DashboardLayout")
public abstract class DashboardLayout extends BaseUuidEntity {
    @MetaProperty
    protected LinkedList<DashboardLayout> children = new LinkedList<>();

    public LinkedList<DashboardLayout> getChildren() {
        return children;
    }

    public void setChildren(LinkedList<DashboardLayout> children) {
        this.children = children;
    }
}
