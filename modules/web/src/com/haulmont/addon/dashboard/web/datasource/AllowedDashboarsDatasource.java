package com.haulmont.addon.dashboard.web.datasource;

import com.haulmont.addon.dashboard.entity.PersistentDashboard;
import com.haulmont.addon.dashboard.service.UserRestrictedEntityService;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.data.impl.CustomCollectionDatasource;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class AllowedDashboarsDatasource extends CustomCollectionDatasource<PersistentDashboard, UUID> {

    private UserRestrictedEntityService userRestrictedEntityService = AppBeans.get(UserRestrictedEntityService.class);

    /**
     * @param params
     * @return List of entities. List size is limited to maxResults, starting form firstResult position
     */
    @Override
    protected Collection<PersistentDashboard> getEntities(Map<String, Object> params) {
        return userRestrictedEntityService.getAllowedDashboards();
    }

    /**
     * @return Count of all entities, stored in database
     */
    @Override
    public int getCount() {
        return userRestrictedEntityService.getAllowedDashboardsCount();
    }

}