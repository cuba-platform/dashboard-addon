/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.tools;

import com.haulmont.addon.dashboard.entity.WidgetTemplate;
import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.cuba.core.global.UserSessionSource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static org.apache.commons.lang.StringUtils.isBlank;

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
