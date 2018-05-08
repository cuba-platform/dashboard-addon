/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard;

import com.audimex.dashboard.model.Dashboard;
import com.audimex.dashboard.model.Parameter;
import com.audimex.dashboard.web.parameter.ParameterBrowse;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.data.Datasource;

import javax.inject.Inject;

import java.util.List;

import static com.audimex.dashboard.web.parameter.ParameterBrowse.PARAMETERS;

public class DashboardEdit extends AbstractEditor<Dashboard> {
    @Inject
    protected Datasource<Dashboard> dashboardDs;
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected ParameterBrowse paramsFrame;

    //The AbstractEditor replaces an item to another object, if one has status '[new]'
    @WindowParam(name = "ITEM", required = true)
    protected Dashboard inputItem;

    @Override
    public void postInit() {
        dashboardDs.setItem(inputItem);
        initParametersFrame();
    }

    @Override
    protected boolean preCommit() {
        List<Parameter> parameters = paramsFrame.getParameters();
        getItem().setParameters(parameters);
        return super.preCommit();
    }

    protected void initParametersFrame() {
        paramsFrame.init(ParamsMap.of(
                PARAMETERS,
                getItem().getParameters()
        ));
    }

}
