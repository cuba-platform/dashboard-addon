/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.widget_types;

import com.audimex.dashboard.model.Parameter;
import com.audimex.dashboard.model.Widget;
import com.haulmont.cuba.gui.components.AbstractFrame;

import java.util.Map;
import java.util.stream.Collectors;

public class AbstractWidgetBrowse extends AbstractFrame {
    public static final String WIDGET = "WIDGET";

    protected Widget widget;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        widget = (Widget) params.get(WIDGET);

        if (widget == null) {
            //todo create DashboardException
            throw new RuntimeException("Can't get a Widget object in input parameters");
        }
    }

    public Map<String, Object> getParamsForFrame() {
        return widget.getParameters().stream()
                .collect(Collectors.toMap(
                        Parameter::getName,
                        parameter -> parameter.getParameterValue().getValue()
                ));
    }

    public Widget getWidget() {
        return widget;
    }

}
