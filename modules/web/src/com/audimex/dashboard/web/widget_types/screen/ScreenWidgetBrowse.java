/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.widget_types.screen;

import com.audimex.dashboard.annotation_analyzer.WidgetTypeAnalyzer;
import com.audimex.dashboard.model.Parameter;
import com.audimex.dashboard.web.widget_types.AbstractWidgetFrame;
import com.haulmont.cuba.gui.components.VBoxLayout;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.audimex.dashboard.model.widget_types.ScreenWidget.CAPTION;

public class ScreenWidgetBrowse extends AbstractWidgetFrame {
    @Inject
    protected VBoxLayout screenBox;
    @Inject
    protected WidgetTypeAnalyzer typeAnalyzer;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        String browseFrameId = getBrowserFrameId();
        List<Parameter> parameters = widget.getParameters();
        openFrame(screenBox, browseFrameId, getParamsForFrame(parameters));
    }

    protected String getBrowserFrameId() {
        return typeAnalyzer.getWidgetTypesInfo().stream()
                .filter(types -> CAPTION.equals(types.getCaption()))
                .findFirst()
                .get()
                .getBrowseFrameId();
    }

    protected Map<String, Object> getParamsForFrame(List<Parameter> parameters) {
        return parameters.stream()
                .collect(Collectors.toMap(
                        Parameter::getName,
                        parameter -> parameter.getParameterValue().getValue()
                ));
    }
}
