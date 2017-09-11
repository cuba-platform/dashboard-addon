/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.tools;

import com.audimex.dashboard.entity.*;
import com.audimex.dashboard.web.model.WidgetLinkModel;
import com.audimex.dashboard.web.model.WidgetModel;
import com.audimex.dashboard.web.model.WidgetParameterModel;
import com.audimex.dashboard.web.widgets.FramePanel;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

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

    protected DashboardWidgetLink createDashboardLink(Dashboard dashboard, DashboardWidget widget) {
        DashboardWidgetLink dashboardWidgetLink = metadata.create(DashboardWidgetLink.class);

        widget.getParameters().forEach(parameter -> {
            WidgetParameter param = createWidgetLinkParameter(dashboardWidgetLink, parameter);
            dashboardWidgetLink.addDashboardParameter(param);
        });

        dashboardWidgetLink.setDashboardWidget(widget);
        dashboardWidgetLink.setDashboard(dashboard);

        return dashboardWidgetLink;
    }

    public WidgetParameter createWidgetLinkParameter(DashboardWidgetLink link, WidgetParameter parameter) {
        WidgetParameter param = metadata.create(WidgetParameter.class);
        param.setName(parameter.getName());
        param.setParameterType(parameter.getParameterType());
        param.setDashboardWidgetLink(link);
        param.getReferenceToEntity().setMetaClassName(parameter.getReferenceToEntity().getMetaClassName());
        param.getReferenceToEntity().setViewName(parameter.getReferenceToEntity().getViewName());
        return param;
    }

    @SuppressWarnings("unchecked")
    public Object getWidgetLinkParameterValue(WidgetParameter widgetParameter) {
        if (widgetParameter.getParameterType().equals(WidgetParameterType.ENTITY)) {
            ReferenceToEntity referenceToEntity = (ReferenceToEntity) widgetParameter.getValue();
            if (referenceToEntity == null || referenceToEntity.getEntityId() == null) return null;

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
            ReferenceToEntity referenceToEntity = widgetParameter.getReferenceToEntity();
            List<WidgetParameter> parameters = (List<WidgetParameter>) widgetParameter.getValue();
            if (referenceToEntity == null || parameters == null || parameters.size() == 0) return null;

            List<UUID> ids = parameters.stream()
                    .map(parameter -> parameter.getReferenceToEntity().getEntityId())
                    .collect(Collectors.toList());

            Metadata metadata = AppBeans.get(Metadata.class);
            Class entityClass = metadata.getSession()
                    .getClassNN(referenceToEntity.getMetaClassName()).getJavaClass();

            LoadContext.Query query = new LoadContext.Query(
                    String.format("select e from %s e where e.id in :ids",
                            referenceToEntity.getMetaClassName())
            ).setParameter("ids", ids);

            LoadContext ctx = LoadContext.create(entityClass)
                    .setQuery(query)
                    .setView(referenceToEntity.getViewName()
            );

            return dataManager.loadList(ctx);
        } else {
            return widgetParameter.getValue();
        }
    }

    public List<WidgetLinkModel> createWidgetLinkModel(FramePanel framePanel) {
        List<WidgetLinkModel> widgetLinksModel = new ArrayList<>();
        framePanel.getWidget().getDashboardLinks().forEach(link -> {
            WidgetLinkModel wlm = new WidgetLinkModel();
            wlm.setDashboard(link.getDashboard().getId());
            wlm.setFilter(link.getFilter());

            List<WidgetParameterModel> widgetParametersModel = new ArrayList<>();
            link.getDashboardParameters().forEach(parameter -> {
                WidgetParameterModel par = createWidgetParameterModel(parameter);
                List<WidgetParameterModel> childWidgetParametersModel = new ArrayList<>();
                if (parameter.getListParameters() != null) {
                    parameter.getListParameters().forEach(lp -> {
                        WidgetParameterModel childPar = createWidgetParameterModel(lp);
                        childWidgetParametersModel.add(childPar);
                    });
                }
                par.setListParameters(childWidgetParametersModel);
                widgetParametersModel.add(par);
            });

            wlm.setDashboardParameters(widgetParametersModel);
            widgetLinksModel.add(wlm);
        });

        return widgetLinksModel;
    }

    protected WidgetParameterModel createWidgetParameterModel(WidgetParameter parameter) {
        WidgetParameterModel wpm = new WidgetParameterModel();
        wpm.setName(parameter.getName());
        wpm.setParameterType(parameter.getParameterType().getId());
        wpm.setIntegerValue(parameter.getIntegerValue());
        wpm.setStringValue(parameter.getStringValue());
        wpm.setDecimalValue(parameter.getDecimalValue());
        wpm.setDateValue(parameter.getDateValue());
        wpm.setBoolValue(parameter.getBoolValue());
        wpm.setLongValue(parameter.getLongValue());
        wpm.setDoubleValue(parameter.getDoubleValue());

        if (parameter.getMasterParameter() != null) {
            wpm.setMasterParameter(parameter.getMasterParameter().getId());
        }

        if (parameter.getAdditionalParameters() != null) {
            parameter.getAdditionalParameters().forEach(ap -> {
                WidgetParameterModel additional = new WidgetParameterModel();
                additional.setName(ap.getName());
                additional.setParameterType(ap.getParameterType().getId());
                additional.setIntegerValue(ap.getIntegerValue());
                additional.setStringValue(ap.getStringValue());
                additional.setDecimalValue(ap.getDecimalValue());
                additional.setDateValue(ap.getDateValue());
                additional.setBoolValue(ap.getBoolValue());
                additional.setLongValue(ap.getLongValue());
                additional.setDoubleValue(ap.getDoubleValue());
                if (additional.getMasterParameter() != null) {
                    additional.setMasterParameter(ap.getMasterParameter().getId());
                }

                ReferenceToEntity reference = ap.getReferenceToEntity();

                additional.setEntityId(reference.getEntityId());
                additional.setMetaClassName(reference.getMetaClassName());
                additional.setViewName(reference.getViewName());
                wpm.addAdditionalParameter(additional);
            });
        }

        ReferenceToEntity reference = parameter.getReferenceToEntity();

        wpm.setEntityId(reference.getEntityId());
        wpm.setMetaClassName(reference.getMetaClassName());
        wpm.setViewName(reference.getViewName());

        return wpm;
    }

    public List<DashboardWidgetLink> loadWidgetLinks(WidgetModel widgetModel) {
        List<DashboardWidgetLink> dashboardWidgetLinks = new ArrayList<>();


        widgetModel.getWidgetLinks().forEach(modelLink -> {
            DashboardWidgetLink dwl = new DashboardWidgetLink();
            dwl.setDashboard(
                    (Dashboard) getEntity(
                            Dashboard.class,
                            modelLink.getDashboard(),
                            "dashboard-view"
                    )
            );
            dwl.setFilter(modelLink.getFilter());

            List<WidgetParameter> widgetParametersModel = new ArrayList<>();
            modelLink.getDashboardParameters().forEach(modelParameter -> {
                WidgetParameter par = createWidgetParameter(modelParameter);

                List<WidgetParameter> childWidgetParameters = new ArrayList<>();
                if (modelParameter.getListParameters() != null) {
                    modelParameter.getListParameters().forEach(mlp -> {
                        WidgetParameter childPar = createWidgetParameter(mlp);
                        childWidgetParameters.add(childPar);
                    });
                }
                par.setListParameters(childWidgetParameters);
                widgetParametersModel.add(par);
            });

            dwl.setDashboardParameters(widgetParametersModel);
            dashboardWidgetLinks.add(dwl);
        });

        return dashboardWidgetLinks;
    }

    protected Object getEntity(Class<? extends BaseUuidEntity> entityClass, UUID uuid, String view) {
        LoadContext ctx = LoadContext.create(entityClass)
                .setId(uuid)
                .setView(view);
        return dataManager.load(ctx);
    }

    protected WidgetParameter createWidgetParameter(WidgetParameterModel parameter) {
        WidgetParameter wp = new WidgetParameter();
        wp.setName(parameter.getName());
        wp.setParameterType(WidgetParameterType.fromId(parameter.getParameterType()));
        wp.setIntegerValue(parameter.getIntegerValue());
        wp.setStringValue(parameter.getStringValue());
        wp.setDecimalValue(parameter.getDecimalValue());
        wp.setDateValue(parameter.getDateValue());
        wp.setBoolValue(parameter.getBoolValue());
        wp.setLongValue(parameter.getLongValue());
        wp.setDoubleValue(parameter.getDoubleValue());
        if (parameter.getMasterParameter() != null) {
            wp.setMasterParameter(
                    (WidgetParameter) getEntity(
                            WidgetParameter.class,
                            parameter.getMasterParameter(),
                            "master-widgetParameter-view"
                    )
            );
        }

        wp.setReferenceToEntity(new ReferenceToEntity());
        wp.getReferenceToEntity().setEntityId(parameter.getEntityId());
        wp.getReferenceToEntity().setMetaClassName(parameter.getMetaClassName());
        wp.getReferenceToEntity().setViewName(parameter.getViewName());

        if (parameter.getAdditionalParameters() != null) {
            parameter.getAdditionalParameters().forEach(ap -> {
                WidgetParameter additional = new WidgetParameter();
                additional.setName(ap.getName());
                additional.setParameterType(WidgetParameterType.fromId(ap.getParameterType()));
                additional.setIntegerValue(ap.getIntegerValue());
                additional.setStringValue(ap.getStringValue());
                additional.setDecimalValue(ap.getDecimalValue());
                additional.setDateValue(ap.getDateValue());
                additional.setBoolValue(ap.getBoolValue());
                additional.setLongValue(ap.getLongValue());
                additional.setDoubleValue(ap.getDoubleValue());
                if (ap.getMasterParameter() != null) {
                    additional.setMasterParameter(
                            (WidgetParameter) getEntity(
                                    WidgetParameter.class,
                                    ap.getMasterParameter(),
                                    "master-widgetParameter-view"
                            )
                    );
                }

                additional.setReferenceToEntity(new ReferenceToEntity());
                additional.getReferenceToEntity().setEntityId(ap.getEntityId());
                additional.getReferenceToEntity().setMetaClassName(ap.getMetaClassName());
                additional.getReferenceToEntity().setViewName(ap.getViewName());
                wp.addAdditionalParameter(additional);
            });
        }

        return wp;
    }

    public Map<String, Object> getParameterValues(DashboardWidget widget) {
        Map<String, Object> params = new HashMap<>();
        for (DashboardWidgetLink link : widget.getDashboardLinks()) {
            for (WidgetParameter parameter : link.getDashboardParameters()) {
                Object value = getWidgetLinkParameterValue(parameter);
                params.put(parameter.getAlias(), value);
            }
        }
        return params;
    }

    public List<String> getUndefinedParameters(Map<String, Object> params) {
        List<String> undefinedParameters = new ArrayList<>();
        for (Map.Entry<String, Object> par : params.entrySet()) {
            if (par.getValue() == null) {
                undefinedParameters.add(par.getKey());
            }
        }
        return undefinedParameters;
    }
}
