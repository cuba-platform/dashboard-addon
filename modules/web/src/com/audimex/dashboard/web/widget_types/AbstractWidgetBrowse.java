/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.widget_types;

import com.audimex.dashboard.model.Dashboard;
import com.audimex.dashboard.model.Parameter;
import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.web.events.WidgetUpdatedEvent;
import com.haulmont.cuba.gui.components.AbstractFrame;
import org.springframework.context.event.EventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

public abstract class AbstractWidgetBrowse extends AbstractFrame implements WidgetBrowse {
    public static final String WIDGET = "WIDGET";
    public static final String DASHBOARD = "DASHBOARD";

    protected Widget widget;
    protected Dashboard dashboard;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        widget = (Widget) params.get(WIDGET);
        dashboard = (Dashboard) params.get(DASHBOARD);

        if (widget == null) {
            throw new RuntimeException("Can't get a Widget object in input parameters");
        }
        if (dashboard == null) {
            throw new RuntimeException("Can't get a Dashboard object in input parameters");
        }
    }

    public Map<String, Object> getParamsForFrame() {
        List<Parameter> unitedParams = new ArrayList<>();
        if (isNotEmpty(dashboard.getParameters())) {
            unitedParams.addAll(dashboard.getParameters());
        }
        if (isNotEmpty(widget.getParameters())) {
            unitedParams.addAll(widget.getParameters());
        }

        Map<String, Object> map = new HashMap<>();
        for (Parameter parameter : unitedParams) {
            map.put(parameter.getName(),
                    parameter.getParameterValue() == null ? null : parameter.getParameterValue().getValue());
        }
        return map;
    }

    @EventListener
    public void onWidgetUpdated(WidgetUpdatedEvent event){
        Widget source = event.getSource();

        if(widget.getId().equals(source.getId())){
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

    @Override
    public abstract void refresh();
}
