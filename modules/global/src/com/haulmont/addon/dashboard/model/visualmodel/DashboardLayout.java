/*
 * Copyright (c) 2008-2018 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.haulmont.addon.dashboard.model.visualmodel;

import com.haulmont.addon.dashboard.utils.DashboardLayoutUtils;
import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@MetaClass(name = "dashboard$DashboardLayout")
public abstract class DashboardLayout extends BaseUuidEntity {
    @MetaProperty
    protected List<DashboardLayout> children = new ArrayList<>();

    /**
     * The expand ratio of given layout in a parent layout.
     */
    @MetaProperty
    protected Integer weight = 1;

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

    public void removeOwnChild(DashboardLayout child) {
        children.remove(child);
    }

    public void removeChild(DashboardLayout child) {
        DashboardLayout parent = findParent(child);
        parent.removeOwnChild(child);
    }

    public void removeChild(UUID childId) {
        DashboardLayout parent = findParent(childId);
        parent.removeOwnChild(findLayout(childId));
    }

    public DashboardLayout findParent(DashboardLayout child) {
        return DashboardLayoutUtils.findParentLayout(this, child.getId());
    }

    public DashboardLayout findParent(UUID childId) {
        return DashboardLayoutUtils.findParentLayout(this, childId);
    }

    public DashboardLayout findLayout(UUID uuid) {
        return DashboardLayoutUtils.findLayout(this, uuid);
    }

    @MetaProperty
    public abstract String getCaption();

    public boolean isRoot() {
        return false;
    }
}
