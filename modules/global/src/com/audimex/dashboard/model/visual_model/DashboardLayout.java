/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.model.visual_model;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

import java.util.LinkedList;
import java.util.UUID;

@MetaClass(name = "amxd$DashboardLayout")
public class DashboardLayout extends BaseUuidEntity {
    @MetaProperty
    protected UUID layoutId;
    @MetaProperty
    protected String layoutType;
    @MetaProperty
    protected LinkedList<DashboardLayout> children = new LinkedList<>();

    public UUID getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(UUID layoutId) {
        this.layoutId = layoutId;
    }

    public String getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(LayoutType layoutType) {
        this.layoutType = layoutType == null ? "" : layoutType.getId();
    }

    public LinkedList<DashboardLayout> getChildren() {
        return children;
    }

    public void setChildren(LinkedList<DashboardLayout> children) {
        this.children = children;
    }
}
