package com.haulmont.addon.dashboard.core.service;

import com.haulmont.addon.dashboard.core.dao.UserRestrictedEntityDao;
import com.haulmont.addon.dashboard.entity.PersistentDashboard;
import com.haulmont.addon.dashboard.entity.WidgetTemplate;
import com.haulmont.addon.dashboard.service.UserRestrictedEntityService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service(UserRestrictedEntityService.NAME)
public class UserRestrictedEntityServiceBean implements UserRestrictedEntityService {

    @Inject
    private UserRestrictedEntityDao userRestrictedEntityDao;

    @Override
    public List<PersistentDashboard> getAllowedDashboards() {
        return userRestrictedEntityDao.getAllowedDashboards();
    }

    @Override
    public List<WidgetTemplate> getAllowedWidgetTemplates() {
        return userRestrictedEntityDao.getAllowedWidgetTemplates();
    }

    @Override
    public int getAllowedDashboardsCount() {
        return userRestrictedEntityDao.getAllowedDashboardsCount();
    }

    @Override
    public int getAllowedWidgetTemplatesCount() {
        return userRestrictedEntityDao.getAllowedWidgetTemplatesCount();
    }
}
