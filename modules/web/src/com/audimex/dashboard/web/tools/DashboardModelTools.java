/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.tools;

import com.audimex.dashboard.entity.Dashboard;
import com.audimex.dashboard.entity.DashboardWidget;
import com.audimex.dashboard.entity.DashboardWidgetLink;
import com.audimex.dashboard.web.model.DashboardModel;
import com.audimex.dashboard.web.model.WidgetLinkModel;
import com.audimex.dashboard.web.model.WidgetModel;
import com.audimex.dashboard.web.model.WidgetParameterModel;
import com.google.gson.Gson;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author bochkarev
 * @version $Id$
 */
@Component(DashboardModelTools.NAME)
public class DashboardModelTools {
    public static final String NAME = "amxd_DashboardModelTools";

    protected Gson gson = new Gson();

    @Inject
    protected DataManager dataManager;

    @Inject
    protected ParameterTools parameterTools;

    public void propagateWidgetChanges(@NotNull DashboardWidget widget) {
        LoadContext<Dashboard> lc = new LoadContext<>(Dashboard.class);
        lc.setView("dashboard-view");
        lc.setQueryString("select d from amxd$Dashboard d where d.model like :widgetId")
                .setParameter("widgetId", "%\"templateWidgetId\":\"" + widget.getId() + "\"%");

        List<Dashboard> dashboards = dataManager.loadList(lc);

        CommitContext cc = new CommitContext();
        dashboards.forEach(dashboard -> {
            DashboardModel dashboardModel = gson.fromJson(dashboard.getModel(), DashboardModel.class);
            List<WidgetModel> widgetModels = dashboardModel.getWidgets().stream()
                    .filter(widgetModel -> widget.getId().equals(widgetModel.getTemplateWidgetId()))
                    .collect(Collectors.toList());

            widgetModels.forEach(widgetModel -> {
                widgetModel.setViewType(widget.getWidgetViewType());
                widgetModel.setEntityType(widget.getEntityType());
                widgetModel.setReportId(widget.getReport() != null ? widget.getReport().getId() : null);
                widgetModel.setIcon(widget.getIcon());
                widgetModel.setCaption(widget.getCaption());
                widgetModel.setFrameId(widget.getFrameId());

                Optional<WidgetLinkModel> optional = widgetModel.getWidgetLinks().stream().findFirst();
                DashboardWidgetLink newLink = parameterTools.createDashboardLink(dashboard, widget);
                WidgetLinkModel wlm = parameterTools.createWidgetLinkModel(newLink);

                if (optional.isPresent()) {
                    WidgetLinkModel oldWlm = optional.get();
                    List<WidgetParameterModel> oldParams = oldWlm.getDashboardParameters();

                    wlm.getDashboardParameters().forEach(np -> oldParams.forEach(oldPar -> {
                        if (np.getName().equals(oldPar.getName()) &&
                                np.getParameterType().equals(oldPar.getParameterType())) {
                            np.setIntegerValue(oldPar.getIntegerValue());
                            np.setStringValue(oldPar.getStringValue());
                            np.setDecimalValue(oldPar.getDecimalValue());
                            np.setDateValue(oldPar.getDateValue());
                            np.setBoolValue(oldPar.getBoolValue());
                            np.setLongValue(oldPar.getLongValue());
                            np.setEntityId(oldPar.getEntityId());
                            np.setDoubleValue(oldPar.getDoubleValue());
                            np.setInputType(oldPar.getInputType());
                            np.setAdditionalParameters(oldPar.getAdditionalParameters());
                        }
                    }));
                }

                widgetModel.setWidgetLinks(new ArrayList<WidgetLinkModel>() {
                    { add(wlm); }
                });
            });

            dashboard.setModel(gson.toJson(dashboardModel));
            cc.addInstanceToCommit(dashboard);
        });

        if (cc.getCommitInstances().size() > 0) {
            dataManager.commit(cc);
        }
    }
}
