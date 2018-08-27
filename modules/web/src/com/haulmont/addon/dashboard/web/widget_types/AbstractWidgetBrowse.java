/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.widget_types;

import com.haulmont.addon.dashboard.gui.components.WidgetBrowse;
import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.Parameter;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.model.param_value_types.ParameterValue;
import com.haulmont.addon.dashboard.web.annotation_analyzer.WidgetRepository;
import com.haulmont.addon.dashboard.web.parameter_transformer.ParameterTransformer;
import com.haulmont.addon.dashboard.web.DashboardException;
import com.haulmont.addon.dashboard.web.events.WidgetUpdatedEvent;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractFrame;
import org.springframework.context.event.EventListener;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

public abstract class AbstractWidgetBrowse extends AbstractFrame implements WidgetBrowse {
    public static final String WIDGET = "WIDGET";
    public static final String DASHBOARD = "DASHBOARD";

    @Inject
    protected ParameterTransformer transformer;

    @Inject
    protected WidgetRepository widgetRepository;

    protected Widget widget;
    protected Dashboard dashboard;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        widget = (Widget) params.get(WIDGET);
        dashboard = (Dashboard) params.get(DASHBOARD);

        widgetRepository.initializeWidgetFields(this, widget);

        if (widget == null) {
            throw new DashboardException("Can't get a Widget object in input parameters");
        }
        if (dashboard == null) {
            throw new DashboardException("Can't get a Dashboard object in input parameters");
        }
    }

    public Map<String, Object> getParamsForFrame() {
        return getParamsForFrame(ParamsMap.empty());
    }

    public Map<String, Object> getParamsForFrame(Map<String, Object> params) {
        Map<String, Object> map = getUnitedParams(params);

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();

            if (value instanceof ParameterValue) {
                entry.setValue(transformer.transform((ParameterValue) value));
            }
        }

        return map;
    }

    protected Map<String, Object> getUnitedParams(Map<String, Object> params) {
        List<Parameter> unitedParams = new ArrayList<>();
        if (isNotEmpty(dashboard.getParameters())) {
            unitedParams.addAll(dashboard.getParameters());
        }
        if (isNotEmpty(widget.getParameters())) {
            unitedParams.addAll(widget.getParameters());
        }

        Map<String, Object> map = unitedParams.stream().collect(
                Collectors.toMap(Parameter::getName, Parameter::getParameterValue, (a, b) -> b));

        map.putAll(params);

        return map;
    }

    @EventListener
    public void onWidgetUpdated(WidgetUpdatedEvent event) {
        Widget source = event.getSource();

        if (widget.getId().equals(source.getId())) {
            widget = source;
            refresh();
        }
    }

    @Override
    public Widget getWidget() {
        return widget;
    }

    @Override
    public Dashboard getDashboard() {
        return dashboard;
    }
}
