/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.tools;

import com.audimex.dashboard.entity.*;
import com.audimex.dashboard.web.dashboard.events.DashboardEvent;
import com.audimex.dashboard.web.dashboard.events.DashboardEventType;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.function.Consumer;

/**
 * @author bochkarev
 * @version $Id$
 */
@Component(ParameterTools.NAME)
public class ParameterTools {
    public static final String NAME = "amxd_ParameterTools";

    @Inject
    protected Metadata metadata;

    @Inject
    protected DataManager dataManager;

    protected DashboardWidgetLink createDashboardLink(Dashboard dashboard, DashboardWidget widget,
                                                      Consumer<DashboardEvent> dashboardEventConsumer) {
        DashboardWidgetLink dashboardWidgetLink = metadata.create(DashboardWidgetLink.class);

        widget.getParameters().forEach(parameter -> {
            WidgetParameter param = createWidgetLinkParameter(dashboardWidgetLink, parameter, widget);
            dashboardWidgetLink.addDashboardParameter(param);
        });

        dashboardWidgetLink.setDashboardWidget(widget);
        dashboardWidgetLink.setDashboard(dashboard);

        if (dashboardEventConsumer != null) {
            dashboardEventConsumer.accept(
                    new DashboardEvent<>(dashboardWidgetLink, DashboardEventType.CREATE)
            );
        }

        if (dashboardEventConsumer != null) {
            dashboardWidgetLink.getDashboardParameters().forEach(param ->
                    dashboardEventConsumer.accept(
                            new DashboardEvent<>(param, DashboardEventType.CREATE)
                    )
            );
        }

        dashboard.addWidgetLink(dashboardWidgetLink);

        return dashboardWidgetLink;
    }

    public WidgetParameter createWidgetLinkParameter(DashboardWidgetLink link, WidgetParameter parameter,
                                                     DashboardWidget widget) {
        WidgetParameter param = metadata.create(WidgetParameter.class);
        param.setName(parameter.getName());
        param.setParameterType(parameter.getParameterType());
        param.setDashboardWidget(widget);
        param.setDashboardWidgetLink(link);
        return param;
    }

    @SuppressWarnings("unchecked")
    public Object getWidgetLinkParameterValue(WidgetParameter widgetParameter) {
        if (widgetParameter.getParameterType().equals(WidgetParameterType.ENTITY)) {
            ReferenceToEntity referenceToEntity = (ReferenceToEntity) widgetParameter.getValue();
            Metadata metadata = AppBeans.get(Metadata.class);
            LoadContext ctx = LoadContext.create(
                                metadata.getSession().getClassNN(
                                        referenceToEntity.getMetaClassName()
                                )
                                .getJavaClass()
                        )
                    .setId(referenceToEntity.getEntityId())
                    .setView(referenceToEntity.getViewName()
            );
            return dataManager.load(ctx);
        } else if (widgetParameter.getParameterType().equals(WidgetParameterType.LIST_ENTITY)) {
            ReferenceToEntity referenceToEntity = (ReferenceToEntity) widgetParameter.getValue();
            Metadata metadata = AppBeans.get(Metadata.class);

            Class entityClass = metadata.getSession().getClassNN(referenceToEntity.getMetaClassName()).getJavaClass();
            LoadContext.Query query = new LoadContext.Query(
                    String.format("select e from %s e where e.id in :ids", referenceToEntity.getMetaClassName())
            ).setParameter("ids", widgetParameter.getValue());

            LoadContext ctx = LoadContext.create(entityClass)
                    .setQuery(query)
                    .setView(referenceToEntity.getViewName()
            );

            return dataManager.loadList(ctx);
        } else {
            return widgetParameter.getValue();
        }
    }
}
