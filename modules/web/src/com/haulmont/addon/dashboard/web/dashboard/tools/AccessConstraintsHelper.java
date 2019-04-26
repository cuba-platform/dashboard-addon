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

package com.haulmont.addon.dashboard.web.dashboard.tools;

import com.haulmont.addon.dashboard.entity.WidgetTemplate;
import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.cuba.core.global.UserSessionSource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class AccessConstraintsHelper {
    @Inject
    protected UserSessionSource sessionSource;

    public boolean isDashboardAllowedCurrentUser(Dashboard dashboard) {
        Boolean isAvailableForAllUsers = dashboard.getIsAvailableForAllUsers();
        String createdBy = dashboard.getCreatedBy();

        if (isAvailableForAllUsers == null || isBlank(createdBy)) {
            return true;
        }

        return isAvailableForAllUsers || getCurrentSessionLogin().equals(createdBy);
    }

    public boolean isWidgetTemplateAllowedCurrentUser(WidgetTemplate widgetTemplate) {
        Boolean isAvailableForAllUsers = widgetTemplate.getIsAvailableForAllUsers();
        String createdBy = widgetTemplate.getCreatedBy();

        if (isAvailableForAllUsers == null || isBlank(createdBy)) {
            return true;
        }

        return isAvailableForAllUsers || getCurrentSessionLogin().equals(createdBy);
    }

    public String getCurrentSessionLogin() {
        return sessionSource.getUserSession().getUser().getLogin();
    }
}
