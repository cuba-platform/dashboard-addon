/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.frames.editor;

import com.haulmont.addon.dashboard.converter.JsonConverter;
import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.web.DashboardException;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasEditorFrame;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.palette.PaletteFrame;
import com.haulmont.addon.dashboard.web.dashboard.tools.AccessConstraintsHelper;
import com.haulmont.addon.dashboard.web.events.DashboardUpdatedEvent;
import com.haulmont.addon.dashboard.web.parameter.ParameterBrowse;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.export.ByteArrayDataProvider;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.addon.dashboard.web.DashboardException;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame;
import com.haulmont.addon.dashboard.web.dashboard.tools.AccessConstraintsHelper;
import com.haulmont.addon.dashboard.web.events.DashboardUpdatedEvent;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.Objects;

import static com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame.DASHBOARD;
import static com.haulmont.addon.dashboard.web.parameter.ParameterBrowse.PARAMETERS;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class DashboardEdit extends AbstractEditor<Dashboard> {
    public static final String SCREEN_NAME = "dashboardEdit";
    public static final String DASHBOARD_EDIT_FIELDGROUP = "amxd$dashboardEditFieldGroup";

    @Inject
    protected Datasource<Dashboard> dashboardDs;
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected GroupBoxLayout paramsBox;
    @Inject
    protected VBoxLayout paletteBox;
    @Inject
    protected VBoxLayout canvasBox;
    @Inject
    protected JsonConverter converter;
    @Inject
    protected Metadata metadata;
    @Inject
    protected ExportDisplay exportDisplay;
    @Inject
    protected FileUploadField importJsonField;
    @Inject
    protected AccessConstraintsHelper accessHelper;
    @Inject
    protected Events events;

    //The AbstractEditor replaces an item to another object, if one has status '[new]'
    @WindowParam(name = "ITEM", required = true)
    protected Dashboard inputItem;

    protected ParameterBrowse parametersFrame;
    protected AbstractFrame paletteFrame;
    protected CanvasEditorFrame canvasFrame;

    @Override
    public void postInit() {
        dashboardDs.setItem(inputItem);
        importJsonField.addFileUploadSucceedListener(e -> uploadJson());
        initParametersFrame();
        initPaletteFrame();
        initCanvasFrame();
    }

    @Override
    public Dashboard getItem() {
        Dashboard dashboard = dashboardDs.getItem();
        dashboard.setParameters(parametersFrame.getParameters());
        dashboard.setVisualModel(canvasFrame.getDashboardModel());
        dashboard.setCreatedBy(accessHelper.getCurrentSessionLogin());
        return dashboard;
    }

    protected void initParametersFrame() {
        parametersFrame = (ParameterBrowse) openFrame(paramsBox, ParameterBrowse.SCREEN_NAME, ParamsMap.of(
                PARAMETERS, dashboardDs.getItem().getParameters()
        ));
    }

    protected void initPaletteFrame() {
        paletteFrame = openFrame(paletteBox, PaletteFrame.SCREEN_NAME);
    }

    protected void initCanvasFrame() {
        canvasFrame = (CanvasEditorFrame) openFrame(canvasBox, CanvasEditorFrame.SCREEN_NAME, ParamsMap.of(
                CanvasFrame.DASHBOARD, dashboardDs.getItem()
        ));
    }

    public void cancel() {
        close("close", true);
    }

    public void onExportJsonBtnClick() {
        String jsonModel = converter.dashboardToJson(getItem());

        if (isNotBlank(jsonModel)) {
            byte[] bytes = jsonModel.getBytes(UTF_8);
            String fileName = isNotBlank(dashboardDs.getItem().getTitle()) ? dashboardDs.getItem().getTitle() : "dashboard";
            exportDisplay.show(new ByteArrayDataProvider(bytes), format("%s.json", fileName));
        }
    }

    protected void uploadJson() {
        try (InputStream fileContent = importJsonField.getFileContent()) {
            String json = IOUtils.toString(Objects.requireNonNull(fileContent), UTF_8);
            Dashboard newDashboard = metadata.create(Dashboard.class);
            BeanUtils.copyProperties(converter.dashboardFromJson(json), newDashboard);

            dashboardDs.setItem(newDashboard);
            initParametersFrame();
            initPaletteFrame();
            canvasFrame.updateLayout(newDashboard);
            dashboardDs.refresh();

        } catch (Exception e) {
            throw new DashboardException("Cannot import data from a file", e);
        }
    }

    public void onPropagateBtnClick() {
        Dashboard dashboard = getItem();
        events.publish(new DashboardUpdatedEvent(dashboard));
    }


    @Override
    protected void postValidate(ValidationErrors errors) {
        //remove validation errors from widget frames
        errors.getAll().removeIf(error -> !DASHBOARD_EDIT_FIELDGROUP.equals(error.component.getParent().getId()));
    }

    @Override
    protected boolean preCommit() {
        //remove ds contexts from widget frames
        dashboardDs.getDsContext().getChildren().removeIf(dsContext ->
                !((dsContext.get("parametersDs") != null && dsContext.get("parametersDs").getMetaClass() != null &&
                        "amxd$Parameter".equals(dsContext.get("parametersDs").getMetaClass().getName())) ||
                        (dsContext.get("widgetTemplatesDs") != null && dsContext.get("widgetTemplatesDs").getMetaClass() != null) &&
                                "amxd$WidgetTemplate".equals(dsContext.get("widgetTemplatesDs").getMetaClass().getName())));
        return true;
    }
}
