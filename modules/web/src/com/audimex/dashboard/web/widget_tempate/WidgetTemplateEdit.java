/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.widget_tempate;

import com.audimex.dashboard.model.Parameter;
import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.web.parameter.ParameterBrowse;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.data.Datasource;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static com.audimex.dashboard.web.parameter.ParameterBrowse.PARAMETERS;

public class WidgetTemplateEdit extends AbstractEditor<Widget> {
    @Inject
    protected Datasource<Widget> widgetDs;
    @Inject
    protected ParameterBrowse paramsFrame;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected void postInit() {
        paramsFrame.init(ParamsMap.of(
                PARAMETERS,
                getItem().getParameters()
        ));
    }

    @Override
    protected boolean preCommit() {
        List<Parameter> parameters = paramsFrame.getParameters();
        widgetDs.getItem().setParameters(parameters);

        return super.preCommit();
    }
}