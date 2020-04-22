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
import com.haulmont.addon.dashboard.entity.WidgetTemplate;
import com.haulmont.addon.dashboard.entity.WidgetTemplateGroup;
import com.haulmont.addon.dashboard.model.Parameter;
import com.haulmont.cuba.security.app.role.AnnotatedRoleDefinition;
import com.haulmont.cuba.security.app.role.annotation.EntityAccess;
import com.haulmont.cuba.security.app.role.annotation.EntityAttributeAccess;
import com.haulmont.cuba.security.app.role.annotation.Role;
import com.haulmont.cuba.security.app.role.annotation.ScreenAccess;
import com.haulmont.cuba.security.entity.EntityOp;
import com.haulmont.cuba.security.role.EntityAttributePermissionsContainer;
import com.haulmont.cuba.security.role.EntityPermissionsContainer;
import com.haulmont.cuba.security.role.ScreenPermissionsContainer;

@Role(name = "Dashboard admin")
public class DashboardAdminRole extends AnnotatedRoleDefinition {
    @ScreenAccess(screenIds = {"dashboard",
            "dashboard$PersistentDashboard.browse",
            "dashboard$WidgetTemplate.browse",
            "dashboard$PersistentDashboard.edit",
            "dashboard$ColspanDialog",
            "dashboard$CssLayoutDialog",
            "dashboard$ExpandDialog",
            "dashboard$GridDialog",
            "dashboard$ResponsiveDialog",
            "dashboard$StyleDialog",
            "dashboard$WeightDialog",
            "dashboard$PersistentDashboard.view",
            "dashboard$ParameterBrowse",
            "dashboard$Parameter.edit",
            "dashboard$DashboardGroup.browse",
            "dashboard$DashboardGroup.edit",
            "dashboard$Widget.edit",
            "dashboard$WidgetTemplate.edit",
            "dashboard$WidgetTemplateGroup.edit",
            "dashboard$WidgetTemplateGroup.browse"
    })
    @Override
    public ScreenPermissionsContainer screenPermissions() {
        return super.screenPermissions();
    }


    @EntityAccess(entityClass = Parameter.class,
            operations = {EntityOp.CREATE, EntityOp.READ, EntityOp.UPDATE, EntityOp.DELETE})
    @EntityAccess(entityClass = WidgetTemplateGroup.class,
            operations = {EntityOp.CREATE, EntityOp.READ, EntityOp.UPDATE, EntityOp.DELETE})
    @EntityAccess(entityClass = WidgetTemplate.class,
            operations = {EntityOp.CREATE, EntityOp.READ, EntityOp.UPDATE, EntityOp.DELETE})
    @EntityAccess(entityClass = DashboardGroup.class,
            operations = {EntityOp.CREATE, EntityOp.READ, EntityOp.UPDATE, EntityOp.DELETE})
    @EntityAccess(entityClass = PersistentDashboard.class,
            operations = {EntityOp.CREATE, EntityOp.READ, EntityOp.UPDATE, EntityOp.DELETE})
    @Override
    public EntityPermissionsContainer entityPermissions() {
        return super.entityPermissions();
    }

    @EntityAttributeAccess(entityClass = Parameter.class, modify = "*")
    @EntityAttributeAccess(entityClass = WidgetTemplateGroup.class, modify = "*")
    @EntityAttributeAccess(entityClass = WidgetTemplate.class, modify = "*")
    @EntityAttributeAccess(entityClass = DashboardGroup.class, modify = "*")
    @EntityAttributeAccess(entityClass = PersistentDashboard.class, modify = "*")
    @Override
    public EntityAttributePermissionsContainer entityAttributePermissions() {
        return super.entityAttributePermissions();
    }
}
