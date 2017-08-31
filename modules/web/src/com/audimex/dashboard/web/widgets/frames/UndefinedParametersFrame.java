/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.widgets.frames;

import com.audimex.dashboard.entity.DashboardWidget;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * @author bochkarev
 * @version $Id$
 */
public class UndefinedParametersFrame extends AbstractFrame {
    public static final String PARAMETER_UNDEFINED_VALUES_LIST = "PARAMETER_UNDEFINED_VALUES_LIST";
    public static final String PARAMETER_WIDGET = "PARAMETER_WIDGET";

    @WindowParam(name = PARAMETER_UNDEFINED_VALUES_LIST)
    protected List<String> undefinedParametersList;

    @WindowParam(name = PARAMETER_WIDGET)
    protected DashboardWidget widget;

    @Inject
    protected VBoxLayout undefinedParametersVbox;

    @Inject
    protected ComponentsFactory componentsFactory;

    @Inject
    protected Label widgetName;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        widgetName.setValue(String.format("<b>%s</b>", widget.getCaption()));

        undefinedParametersList.stream()
                .sorted(String::compareTo)
                .forEach(name -> {
                    Label label = componentsFactory.createComponent(Label.class);
                    label.setValue(name);
                    label.setWidth("100%");
                    undefinedParametersVbox.add(label);
                });
    }
}
