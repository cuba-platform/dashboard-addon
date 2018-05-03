/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.widget_types.screen;

import com.audimex.dashboard.model.Parameter;
import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.web.widget_types.AbstractWidget;
import com.haulmont.cuba.gui.components.Frame;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

public class ScreenWidgetBrowse extends AbstractWidget {
    @Inject
    protected Frame screenFrame;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        List<Parameter> parameters = widget.getParameters();
//        parameters.
    }
}
