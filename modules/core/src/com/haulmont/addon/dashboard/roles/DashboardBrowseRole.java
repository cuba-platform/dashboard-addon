/*
 * Copyright (c) 2008-2020 Haulmont.
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

package com.haulmont.addon.dashboard.roles;

import com.haulmont.addon.dashboard.entity.DashboardGroup;
import com.haulmont.addon.dashboard.entity.PersistentDashboard;
import com.haulmont.cuba.security.app.role.AnnotatedRoleDefinition;
import com.haulmont.cuba.security.app.role.annotation.*;
import com.haulmont.cuba.security.entity.EntityOp;
import com.haulmont.cuba.security.role.EntityAttributePermissionsContainer;
import com.haulmont.cuba.security.role.EntityPermissionsContainer;
import com.haulmont.cuba.security.role.ScreenComponentPermissionsContainer;
import com.haulmont.cuba.security.role.ScreenPermissionsContainer;

@Role(name = "Dashboard browse")
public class DashboardBrowseRole extends AnnotatedRoleDefinition {

    @ScreenAccess(screenIds = {"dashboard",
            "dashboard$PersistentDashboard.browse",
            "dashboard$PersistentDashboard.view"})
    @Override
    public ScreenPermissionsContainer screenPermissions() {
        return super.screenPermissions();
    }

    @EntityAccess(entityClass = DashboardGroup.class,
            operations = {EntityOp.READ})
    @EntityAccess(entityClass = PersistentDashboard.class,
            operations = {EntityOp.READ})
    @Override
    public EntityPermissionsContainer entityPermissions() {
        return super.entityPermissions();
    }

    @EntityAttributeAccess(entityClass = DashboardGroup.class, modify = "*")
    @EntityAttributeAccess(entityClass = PersistentDashboard.class, modify = "*")
    @Override
    public EntityAttributePermissionsContainer entityAttributePermissions() {
        return super.entityAttributePermissions();
    }

    @ScreenComponentAccess(screenId = "dashboard$PersistentDashboard.browse", deny = {"create", "edit", "remove", "createBtn", "editBtn", "removeBtn", "dashboardGroupsBrowse"})
    @Override
    public ScreenComponentPermissionsContainer screenComponentPermissions() {
        return super.screenComponentPermissions();
    }
}
