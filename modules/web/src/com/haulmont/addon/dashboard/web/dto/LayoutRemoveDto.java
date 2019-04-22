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

package com.haulmont.addon.dashboard.web.dto;

import com.haulmont.addon.dashboard.model.visualmodel.DashboardLayout;

import java.util.UUID;

public class LayoutRemoveDto {
    private final DashboardLayout dashboardLayout;
    private final UUID removedUuid;
    private final UUID selectUuid;

    public LayoutRemoveDto(DashboardLayout dashboardLayout, UUID removedUuid, UUID selectUuid) {
        this.dashboardLayout = dashboardLayout;
        this.removedUuid = removedUuid;
        this.selectUuid = selectUuid;
    }

    public DashboardLayout getDashboardLayout() {
        return dashboardLayout;
    }

    public UUID getRemovedUuid() {
        return removedUuid;
    }

    public UUID getSelectUuid() {
        return selectUuid;
    }
}
