/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard;

import com.audimex.dashboard.converter.JsonConverter;
import com.audimex.dashboard.entity.WidgetTemplate;
import com.audimex.dashboard.model.Dashboard;
import com.audimex.dashboard.model.Parameter;
import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.model.visual_model.VerticalLayout;
import com.audimex.dashboard.web.dashboard.frames.canvas.CanvasFrame;
import com.audimex.dashboard.web.parameter.ParameterBrowse;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;

import javax.inject.Inject;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.audimex.dashboard.web.dashboard.frames.canvas.CanvasFrame.VISUAL_MODEL;
import static com.audimex.dashboard.web.dashboard.frames.palette.PaletteFrame.WIDGETS;
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
    @Inject
    protected CollectionDatasource<WidgetTemplate, UUID> widgetTemplatesDs;
    @Inject
    protected JsonConverter converter;

    //The AbstractEditor replaces an item to another object, if one has status '[new]'
    @WindowParam(name = "ITEM", required = true)
    protected Dashboard inputItem;

    protected List<Widget> widgetTemplates;
    protected AbstractFrame paletteFrame;
    protected CanvasFrame canvasFrame;

    @Override
    public void postInit() {
        dashboardDs.setItem(inputItem);
        widgetTemplates = getWidgetTemplates();
        initParametersFrame();
        initPaletteFrame();
        initCanvasFrame();
    }

    @Override
    protected boolean preCommit() {
        List<Parameter> parameters = paramsFrame.getParameters();
        getItem().setParameters(parameters);

        VerticalLayout model = canvasFrame.getDashboardModel();
        getItem().setVisualModel(model);

        return super.preCommit();
    }

    protected List<Widget> getWidgetTemplates() {
        widgetTemplatesDs.refresh();
        return widgetTemplatesDs.getItems().stream()
                .map(widgetTemplate -> converter.widgetFromJson(widgetTemplate.getWidgetModel()))
                .collect(Collectors.toList());
    }

    protected void initParametersFrame() {
        paramsFrame.init(ParamsMap.of(
                PARAMETERS,
                getItem().getParameters()
        ));
    }

    protected void initPaletteFrame() {
        paletteFrame = openFrame(paletteBox, "paletteFrame", ParamsMap.of(
                WIDGETS, widgetTemplates
        ));
    }

    protected void initCanvasFrame() {
        canvasFrame = (CanvasFrame) openFrame(canvasBox, "canvasFrame", ParamsMap.of(
                VISUAL_MODEL, getItem().getVisualModel()
        ));
    }
}
