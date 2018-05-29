/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.frames.editor;

import com.audimex.dashboard.converter.JsonConverter;
import com.audimex.dashboard.entity.DashboardPersist;
import com.audimex.dashboard.entity.WidgetTemplate;
import com.audimex.dashboard.model.Dashboard;
import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.web.dashboard.frames.canvas.CanvasEditorFrame;
import com.audimex.dashboard.web.dashboard.frames.editor.palette.PaletteFrame;
import com.audimex.dashboard.web.parameter.ParameterBrowse;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.GroupBoxLayout;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.audimex.dashboard.web.dashboard.frames.canvas.CanvasFrame.VISUAL_MODEL;
import static com.audimex.dashboard.web.dashboard.frames.editor.palette.PaletteFrame.WIDGETS;
import static com.audimex.dashboard.web.parameter.ParameterBrowse.PARAMETERS;

public class DashboardEdit extends AbstractEditor<Dashboard> {
    @Inject
    protected Datasource<Dashboard> dashboardDs;
    @Inject
    protected CollectionDatasource<DashboardPersist, UUID> persDashboardsDs;
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected ParameterBrowse paramsFrame;
    @Inject
    protected GroupBoxLayout paletteBox;
    @Inject
    protected GroupBoxLayout canvasBox;
    @Inject
    protected CollectionDatasource<WidgetTemplate, UUID> widgetTemplatesDs;
    @Inject
    protected JsonConverter converter;
    @Inject
    protected Metadata metadata;

    //The AbstractEditor replaces an item to another object, if one has status '[new]'
    @WindowParam(name = "ITEM", required = true)
    protected Dashboard inputItem;

    protected List<Widget> widgetTemplates;
    protected AbstractFrame paletteFrame;
    protected CanvasEditorFrame canvasFrame;

    @Override
    public void postInit() {
        dashboardDs.setItem(inputItem);
        widgetTemplates = getWidgetTemplates();
        initParametersFrame();
        initPaletteFrame();
        initCanvasFrame();
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
        paletteFrame = openFrame(paletteBox, PaletteFrame.SCREEN_NAME, ParamsMap.of(
                WIDGETS, widgetTemplates
        ));
    }

    protected void initCanvasFrame() {
        canvasFrame = (CanvasEditorFrame) openFrame(canvasBox, CanvasEditorFrame.SCREEN_NAME, ParamsMap.of(
                VISUAL_MODEL, getItem().getVisualModel()
        ));
    }

    public void applyAndClose() {
        saveDashboard();
        commitAndClose();
    }

    public void apply() {
        saveDashboard();
        commit();
    }

    public void cancel() {
        commitAndClose();
    }

    protected void saveDashboard() {
        Dashboard dashboard = getItem();
        dashboard.setParameters(paramsFrame.getParameters());
        dashboard.setVisualModel(canvasFrame.getDashboardModel());

        UUID dashId = dashboard.getId();
        String jsonModel = converter.dashboardToJson(dashboard);

        persDashboardsDs.refresh();
        Optional<DashboardPersist> persDashOpt = persDashboardsDs.getItems().stream()
                .filter(item -> dashId.equals(item.getId()))
                .findFirst();

        if (persDashOpt.isPresent()) {
            DashboardPersist persDash = persDashOpt.get();
            persDash.setDashboardModel(jsonModel);
            persDashboardsDs.updateItem(persDash);
        } else {
            DashboardPersist persDash = metadata.create(DashboardPersist.class);
            persDash.setId(dashId);
            persDash.setDashboardModel(jsonModel);
            persDashboardsDs.addItem(persDash);
        }

        persDashboardsDs.commit();
        persDashboardsDs.refresh();
    }
}
