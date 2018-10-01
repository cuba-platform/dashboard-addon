package com.haulmont.addon.dashboard.core.dao;

import com.haulmont.addon.dashboard.entity.PersistentDashboard;
import com.haulmont.addon.dashboard.entity.WidgetTemplate;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.global.UserSessionSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

import static com.haulmont.addon.dashboard.core.dao.UserRestrictedEntityDao.NAME;

@Component(NAME)
public class UserRestrictedEntityDao {
    public final static String NAME = "dashboard_UserRestrictedEntityDao";

    @Inject
    protected UserSessionSource sessionSource;

    @Inject
    private Persistence persistence;

    @Transactional(readOnly = true)
    public List<PersistentDashboard> getAllowedDashboards() {
        TypedQuery<PersistentDashboard> query = persistence.getEntityManager()
                .createQuery("select dash from dashboard$PersistentDashboard dash " +
                        "where dash.isAvailableForAllUsers = true or dash.createdBy = :login",
                        PersistentDashboard.class);
        query.setParameter("login", getCurrentSessionLogin());
        query.setViewName("dashboard-group-view");

        return query.getResultList();
    }

    @Transactional(readOnly = true)
    public int getAllowedDashboardsCount() {
        Query query = persistence.getEntityManager()
                .createQuery("select count(dash.id) from dashboard$PersistentDashboard dash " +
                        "where dash.isAvailableForAllUsers = true or dash.createdBy = :login");
        query.setParameter("login", getCurrentSessionLogin());
        return (int) query.getSingleResult();
    }

    @Transactional(readOnly = true)
    public List<WidgetTemplate> getAllowedWidgetTemplates() {
        TypedQuery<WidgetTemplate> query = persistence.getEntityManager()
                .createQuery("select wt from dashboard$WidgetTemplate wt " +
                        "where wt.isAvailableForAllUsers = true or wt.createdBy = :login",
                        WidgetTemplate.class);
        query.setParameter("login", getCurrentSessionLogin());
        query.setViewName("widget-group-view");

        return query.getResultList();
    }

    @Transactional(readOnly = true)
    public int getAllowedWidgetTemplatesCount() {
        Query query = persistence.getEntityManager()
                .createQuery("select count(wt.id) from dashboard$WidgetTemplate wt where wt.isAvailableForAllUsers = true or wt.createdBy = :login");
        query.setParameter("login", getCurrentSessionLogin());
        return (int) query.getSingleResult();
    }

    private String getCurrentSessionLogin() {
        return sessionSource.getUserSession().getUser().getLogin();
    }


}
