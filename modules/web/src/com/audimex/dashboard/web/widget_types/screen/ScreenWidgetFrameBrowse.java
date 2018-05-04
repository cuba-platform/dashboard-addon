/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.widget_types.screen;

import com.audimex.dashboard.model.Parameter;
import com.audimex.dashboard.web.widget_types.AbstractWidgetFrame;
import com.haulmont.cuba.gui.components.Frame;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

public class ScreenWidgetFrameBrowse extends AbstractWidgetFrame {
    @Inject
    protected Frame screenFrame;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        List<Parameter> parameters = widget.getParameters();
//        parameters.
    }
}
