package com.haulmont.addon.dashboard.service;

import com.haulmont.addon.dashboard.entity.PersistentDashboard;
import com.haulmont.addon.dashboard.entity.WidgetTemplate;
import java.util.List;

public interface UserRestrictedEntityService {
    String NAME = "dashboard_UserRestrictedEntityService";

    /**
     * Returns dashboards allowed for current user/
     */
    List<PersistentDashboard> getAllowedDashboards();

    /**
     * Returns widget templates allowed for current user
     */
    List<WidgetTemplate> getAllowedWidgetTemplates();

    /**
     * Count dashboards allowed for current user/
     */
    int getAllowedDashboardsCount();

    /**
     * Count widget templates allowed for current user
     */
    int getAllowedWidgetTemplatesCount();

}
