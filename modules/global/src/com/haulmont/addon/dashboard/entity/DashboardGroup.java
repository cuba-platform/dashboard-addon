/*
 * Copyright (c) 2008-2019 Haulmont.
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
 */
package com.haulmont.addon.dashboard.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * The persistence dictionary of the {@link PersistentDashboard}
 */
@NamePattern("%s|name")
@Table(name = "DASHBOARD_DASHBOARD_GROUP")
@Entity(name = "dashboard$DashboardGroup")
public class DashboardGroup extends StandardEntity {
    private static final long serialVersionUID = 7160480278020869794L;

    @NotNull
    @Column(name = "NAME", nullable = false, unique = true)
    protected String name;

    @OneToMany(mappedBy = "group")
    protected List<PersistentDashboard> dashboards;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDashboards(List<PersistentDashboard> dashboards) {
        this.dashboards = dashboards;
    }

    public List<PersistentDashboard> getDashboards() {
        return dashboards;
    }
}