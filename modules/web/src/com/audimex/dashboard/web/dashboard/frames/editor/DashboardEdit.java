/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.frames.editor;

import com.audimex.dashboard.converter.JsonConverter;
import com.audimex.dashboard.entity.WidgetTemplate;
import com.audimex.dashboard.model.Dashboard;
import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.web.dashboard.events.DashboardUpdatedEvent;
import com.audimex.dashboard.web.dashboard.frames.canvas.CanvasEditorFrame;
import com.audimex.dashboard.web.dashboard.frames.editor.palette.PaletteFrame;
import com.audimex.dashboard.web.parameter.ParameterBrowse;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.export.ByteArrayDataProvider;
import com.haulmont.cuba.gui.export.ExportDisplay;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.audimex.dashboard.web.dashboard.frames.canvas.CanvasFrame.DASHBOARD;
import static com.audimex.dashboard.web.dashboard.frames.editor.palette.PaletteFrame.WIDGETS;
import static com.audimex.dashboard.web.parameter.ParameterBrowse.PARAMETERS;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class DashboardEdit extends AbstractEditor<Dashboard> {
    public static final String SCREEN_NAME = "dashboardEdit";

    @Inject
    protected Datasource<Dashboard> dashboardDs;
    @Inject
    protected CollectionDatasource<WidgetTemplate, UUID> widgetTemplatesDs;
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected GroupBoxLayout paramsBox;
    @Inject
    protected GroupBoxLayout paletteBox;
    @Inject
    protected GroupBoxLayout canvasBox;
    @Inject
    protected JsonConverter converter;
    @Inject
    protected Metadata metadata;
    @Inject
    protected ExportDisplay exportDisplay;
    @Inject
    protected FileUploadField importJsonField;
    @Inject
    protected UserSessionSource sessionSource;
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

    public Dashboard getDashboard() {
        Dashboard dashboard = dashboardDs.getItem();
        dashboard.setParameters(parametersFrame.getParameters());
        dashboard.setVisualModel(canvasFrame.getDashboardModel());

        String currentUserLogin = sessionSource.getUserSession().getUser().getLogin();
        dashboard.setCreatedBy(currentUserLogin);
        return dashboard;
    }

    protected List<Widget> getWidgetTemplates() {
        widgetTemplatesDs.refresh();
        return widgetTemplatesDs.getItems().stream()
                .map(widgetTemplate -> converter.widgetFromJson(widgetTemplate.getWidgetModel()))
                .collect(Collectors.toList());
    }

    protected void initParametersFrame() {
        parametersFrame = (ParameterBrowse) openFrame(paramsBox, ParameterBrowse.SCREEN_NAME, ParamsMap.of(
                PARAMETERS, dashboardDs.getItem().getParameters()
        ));
    }

    protected void initPaletteFrame() {
        paletteFrame = openFrame(paletteBox, PaletteFrame.SCREEN_NAME, ParamsMap.of(
                WIDGETS, getWidgetTemplates()
        ));
    }

    protected void initCanvasFrame() {
        canvasFrame = (CanvasEditorFrame) openFrame(canvasBox, CanvasEditorFrame.SCREEN_NAME, ParamsMap.of(
                DASHBOARD, dashboardDs.getItem()
        ));
    }

    public void cancel() {
        close("close", true);
    }

    @Override
    protected boolean preClose(String actionId) {
        return close(actionId, true);
    }

    public void onExportJsonBtnClick() {
        String jsonModel = converter.dashboardToJson(getDashboard());

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
            throw new RuntimeException("Cannot import data from a file", e);
        }
    }

    public void onPropagateBtnClick() {
        Dashboard dashboard = getDashboard();
        events.publish(new DashboardUpdatedEvent(dashboard));
    }
}
