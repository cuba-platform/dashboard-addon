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
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.gui.data.Datasource;

import javax.inject.Inject;

import java.util.List;

import static com.audimex.dashboard.web.dashboard.frames.PaletteFrame.WIDGETS;
import static com.audimex.dashboard.web.parameter.ParameterBrowse.PARAMETERS;

public class DashboardEdit extends AbstractEditor<Dashboard> {
    @Inject
    protected Datasource<Dashboard> dashboardDs;
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected ParameterBrowse paramsFrame;
    @Inject
    protected VBoxLayout paletteBox;
    @Inject
    protected VBoxLayout canvasBox;

    //The AbstractEditor replaces an item to another object, if one has status '[new]'
    @WindowParam(name = "ITEM", required = true)
    protected Dashboard inputItem;

    @Override
    public void postInit() {
        dashboardDs.setItem(inputItem);
        initParametersFrame();
        initPaletteFrame();
        initCanvasFrame();
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

    protected  void initPaletteFrame(){
        //todo add widgets to params
        AbstractFrame paletteFrame = openFrame(paletteBox, "paletteFrame", ParamsMap.of(
                WIDGETS, null
        ));
    }

    protected  void initCanvasFrame(){
        //todo add dashboardDs link to parameters
        AbstractFrame paletteFrame = openFrame(canvasBox, "canvasFrame");
    }
}
