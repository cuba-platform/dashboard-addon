/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.visual_model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class DashboardLayout implements Serializable {
    protected UUID layoutId;
    protected Integer weight = 1;
    protected String layoutType;
    protected LinkedList<DashboardLayout> children = new LinkedList<>();

    public UUID getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(UUID layoutId) {
        this.layoutId = layoutId;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
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
