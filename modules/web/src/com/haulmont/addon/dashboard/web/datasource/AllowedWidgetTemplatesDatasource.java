package com.haulmont.addon.dashboard.web.datasource;

import com.haulmont.addon.dashboard.entity.WidgetTemplate;
import com.haulmont.addon.dashboard.service.UserRestrictedEntityService;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.data.impl.CustomCollectionDatasource;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class AllowedWidgetTemplatesDatasource extends CustomCollectionDatasource<WidgetTemplate, UUID> {

    private UserRestrictedEntityService userRestrictedEntityService = AppBeans.get(UserRestrictedEntityService.class);

    /**
     * @param params
     * @return List of entities. List size is limited to maxResults, starting form firstResult position
     */
    @Override
    protected Collection<WidgetTemplate> getEntities(Map<String, Object> params) {
        return userRestrictedEntityService.getAllowedWidgetTemplates();
    }

    /**
     * @return Count of all entities, stored in database
     */
    @Override
    public int getCount() {
        return userRestrictedEntityService.getAllowedWidgetTemplatesCount();
    }

}