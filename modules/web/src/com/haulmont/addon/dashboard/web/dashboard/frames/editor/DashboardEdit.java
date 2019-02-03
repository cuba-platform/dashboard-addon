/*
 * Copyright (c) 2008-2018 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.haulmont.addon.dashboard.web.dashboard.frames.editor;

import com.haulmont.addon.dashboard.entity.PersistentDashboard;
import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.model.visualmodel.*;
import com.haulmont.addon.dashboard.web.DashboardException;
import com.haulmont.addon.dashboard.web.DashboardStyleConstants;
import com.haulmont.addon.dashboard.web.dashboard.assistant.DashboardViewAssistant;
import com.haulmont.addon.dashboard.web.dashboard.converter.JsonConverter;
import com.haulmont.addon.dashboard.web.dashboard.events.DashboardRefreshEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.WidgetAddedEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.WidgetMovedEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.model.*;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasEditorFrame;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.colspan.ColspanDialog;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.expand.ExpandDialog;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.palette.PaletteFrame;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.responsive.ResponsiveCreationDialog;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.style.StyleDialog;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.weight.WeightDialog;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasLayout;
import com.haulmont.addon.dashboard.web.dashboard.tools.AccessConstraintsHelper;
import com.haulmont.addon.dashboard.web.dashboard.tools.DashboardModelConverter;
import com.haulmont.addon.dashboard.web.dashboard.tools.DropLayoutTools;
import com.haulmont.addon.dashboard.web.events.DashboardUpdatedEvent;
import com.haulmont.addon.dashboard.web.parameter.ParameterBrowse;
import com.haulmont.addon.dashboard.web.widget.WidgetEdit;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.AbstractDatasource;
import com.haulmont.cuba.gui.export.ByteArrayDataProvider;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.cookie.SM;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.AbstractApplicationContext;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static com.haulmont.addon.dashboard.utils.DashboardLayoutUtils.*;
import static com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame.DASHBOARD;
import static com.haulmont.addon.dashboard.web.parameter.ParameterBrowse.PARAMETERS;
import static com.haulmont.cuba.gui.WindowManager.OpenType.DIALOG;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.strangeway.responsive.web.components.ResponsiveLayout.DisplaySize.*;

public class DashboardEdit extends AbstractEditor<PersistentDashboard> {

    @Inject
    protected Datasource<Dashboard> dashboardDs;
    @Named("dashboard$dashboardEditFieldGroup2")
    protected FieldGroup fieldGroup2;
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
    @Inject
    protected ComponentsFactory componentsFactory;

    protected ParameterBrowse parametersFrame;
    protected AbstractFrame paletteFrame;
    protected CanvasEditorFrame canvasFrame;

    @Named("dropModelConverter")
    protected DashboardModelConverter modelConverter;

    protected DropLayoutTools dropLayoutTools;

    @Named("isAvailableForAllUsersCheckBox")
    protected CheckBox availableCheckBox;

    @Override
    public void postInit() {
        if (PersistenceHelper.isNew(getItem())) {
            Dashboard model = metadata.create(Dashboard.class);
            model.setVisualModel(metadata.create(RootLayout.class));
            model.setCreatedBy(accessHelper.getCurrentSessionLogin());
            dashboardDs.setItem(model);
        } else {
            Dashboard model = converter.dashboardFromJson(getItem().getDashboardModel());
            dashboardDs.setItem(model);
        }
        String assistantBeanName = getDashboard().getAssistantBeanName();
        if (StringUtils.isNotEmpty(assistantBeanName)) {
            dashboardDs.getItem().setAssistantBeanName(assistantBeanName);

            FieldGroup.FieldConfig assistantBeanNameField = fieldGroup2.getField("assistantBeanName");
            LookupField lookupField = (LookupField) assistantBeanNameField.getComponent();
            lookupField.setValue(assistantBeanName);
        }

        if (!accessHelper.getCurrentSessionLogin().equals(dashboardDs.getItem().getCreatedBy())) {
            availableCheckBox.setVisible(false);
        }
        importJsonField.addFileUploadSucceedListener(e -> uploadJson());
        dropLayoutTools = new DropLayoutTools(this, modelConverter, dashboardDs);
        initParametersFrame();
        initPaletteFrame();
        initCanvasFrame();

        ((AbstractDatasource) dashboardDs).setModified(false);
    }

    public Dashboard getDashboard() {
        return dashboardDs.getItem();
    }

    protected void initParametersFrame() {
        parametersFrame = (ParameterBrowse) openFrame(paramsBox, ParameterBrowse.SCREEN_NAME, ParamsMap.of(
                PARAMETERS, getDashboard().getParameters(), DASHBOARD, getDashboard()
        ));
    }

    protected void initPaletteFrame() {
        paletteFrame = openFrame(paletteBox, PaletteFrame.SCREEN_NAME, ParamsMap.of(
                DASHBOARD, getDashboard()
        ));
    }

    protected void initCanvasFrame() {
        canvasFrame = (CanvasEditorFrame) openFrame(canvasBox, CanvasEditorFrame.SCREEN_NAME, ParamsMap.of(
                DASHBOARD, getDashboard()
        ));
        canvasBox.expand(canvasFrame);
    }

    public void cancel() {
        close("close", false);
    }

    public void onExportJsonBtnClick() {
        String jsonModel = converter.dashboardToJson(getDashboard());

        if (isNotBlank(jsonModel)) {
            byte[] bytes = jsonModel.getBytes(UTF_8);
            String fileName = isNotBlank(getDashboard().getTitle()) ? getDashboard().getTitle() : "dashboard";
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
        Dashboard dashboard = getDashboard();
        events.publish(new DashboardUpdatedEvent(dashboard));
    }


    @Override
    protected boolean preCommit() {
        //remove ds contexts from widget frames
        dashboardDs.getDsContext().getChildren().removeIf(dsContext ->
                !((dsContext.get("parametersDs") != null && dsContext.get("parametersDs").getMetaClass() != null &&
                        "dashboard$Parameter".equals(dsContext.get("parametersDs").getMetaClass().getName())) ||
                        (dsContext.get("widgetTemplatesDs") != null && dsContext.get("widgetTemplatesDs").getMetaClass() != null) &&
                                "dashboard$WidgetTemplate".equals(dsContext.get("widgetTemplatesDs").getMetaClass().getName())));

        FieldGroup.FieldConfig assistantBeanName = fieldGroup2.getField("assistantBeanName");
        LookupField lookupField = (LookupField) assistantBeanName.getComponent();
        String val = lookupField.getValue();
        dashboardDs.getItem().setAssistantBeanName(val);

        PersistentDashboard persDash = getItem();
        Dashboard dashboard = getDashboard();
        String jsonModel = converter.dashboardToJson(dashboard);
        persDash.setDashboardModel(jsonModel);
        persDash.setName(dashboard.getTitle());
        persDash.setCode(dashboard.getCode());
        persDash.setIsAvailableForAllUsers(dashboard.getIsAvailableForAllUsers());
        return true;
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed) {
            events.publish(new DashboardUpdatedEvent(getDashboard()));
        }
        return super.postCommit(committed, close);
    }

    public Component generateAssistanceBeanNameField(Datasource<Dashboard> datasource, String fieldId) {
        Map<String, DashboardViewAssistant> assistantBeanMap = AppBeans.getAll(DashboardViewAssistant.class);
        BeanFactory bf = ((AbstractApplicationContext) AppContext.getApplicationContext()).getBeanFactory();
        List<String> prototypeBeanNames = assistantBeanMap.keySet().stream()
                .filter(bn -> bf.isPrototype(bn))
                .collect(toList());
        LookupField lookupField = componentsFactory.createComponent(LookupField.class);
        lookupField.setOptionsList(prototypeBeanNames);
        return lookupField;
    }

    @EventListener
    public void onWidgetMoved(WidgetMovedEvent event) {
        UUID targetLayoutId = event.getParentLayoutUuid();
        Dashboard dashboard = getDashboard();

        dropLayoutTools.moveComponent(event.getSource(), targetLayoutId, event.getLocation());
        events.publish(new DashboardRefreshEvent(dashboard.getVisualModel(), event.getSource().getId()));
        ((AbstractDatasource) dashboardDs).setModified(true);
    }

    @EventListener
    public void onWeightChanged(WeightChangedEvent event) {
        DashboardLayout source = event.getSource();

        if (isParentResponsiveLayout(source)) {
            ResponsiveLayout parentLayout = (ResponsiveLayout) findParentLayout(getDashboard().getVisualModel(), source.getUuid());
            ResponsiveArea responsiveArea = parentLayout.getAreas().stream().
                    filter(ra -> source.getUuid().equals(ra.getComponent().getUuid())).
                    findFirst().orElseThrow(() -> new RuntimeException("Can't find layout with uuid " + source.getUuid()));

            ResponsiveCreationDialog dialog = (ResponsiveCreationDialog) frame.openWindow(ResponsiveCreationDialog.SCREEN_NAME, DIALOG, getDisplaySizeMap(responsiveArea));
            dialog.addCloseListener(actionId -> {
                if (Window.COMMIT_ACTION_ID.equals(actionId)) {
                    int xs = dialog.getXs();
                    int sm = dialog.getSm();
                    int md = dialog.getMd();
                    int lg = dialog.getLg();

                    responsiveArea.setXs(xs);
                    responsiveArea.setSm(sm);
                    responsiveArea.setMd(md);
                    responsiveArea.setLg(lg);
                    events.publish(new DashboardRefreshEvent(getDashboard().getVisualModel(), source.getUuid()));
                }
            });
        } else {
            WeightDialog weightDialog = (WeightDialog) openWindow(WeightDialog.SCREEN_NAME, DIALOG, ParamsMap.of(
                    WeightDialog.WIDGET, source));
            weightDialog.addCloseListener(actionId -> {
                if (Window.COMMIT_ACTION_ID.equals(actionId)) {
                    events.publish(new DashboardRefreshEvent(getDashboard().getVisualModel(), source.getUuid()));
                }
            });
        }
        ((AbstractDatasource) dashboardDs).setModified(true);
    }

    @EventListener
    public void onColspanChanged(ColspanChangedEvent event) {
        DashboardLayout source = event.getSource();

        ColspanDialog weightDialog = (ColspanDialog) openWindow(ColspanDialog.SCREEN_NAME, DIALOG, ParamsMap.of(
                ColspanDialog.WIDGET, source));
        weightDialog.addCloseListener(actionId -> {
            if (Window.COMMIT_ACTION_ID.equals(actionId)) {
                events.publish(new DashboardRefreshEvent(getDashboard().getVisualModel(), source.getUuid()));
            }
        });
        ((AbstractDatasource) dashboardDs).setModified(true);
    }

    @EventListener
    public void onExpandChanged(ExpandChangedEvent event) {
        DashboardLayout source = event.getSource();

        ExpandDialog expandDialog = (ExpandDialog) openWindow(ExpandDialog.SCREEN_NAME, DIALOG, ParamsMap.of(
                ExpandDialog.EXPAND, source.getExpand(),
                ExpandDialog.LAYOUT, source));
        expandDialog.addCloseListener(actionId -> {
            if (Window.COMMIT_ACTION_ID.equals(actionId)) {
                DashboardLayout expandLayout = expandDialog.getExpand();
                if (expandLayout != null) {
                    source.setExpand(expandLayout.getId());
                } else {
                    source.setExpand(null);
                }
                events.publish(new DashboardRefreshEvent(getDashboard().getVisualModel(), source.getUuid()));
            }
        });
        ((AbstractDatasource) dashboardDs).setModified(true);
    }

    @EventListener
    public void onStyleChanged(StyleChangedEvent event) {
        DashboardLayout source = event.getSource();

        StyleDialog weightDialog = (StyleDialog) openWindow(StyleDialog.SCREEN_NAME, DIALOG, ParamsMap.of(
                StyleDialog.STYLENAME, source.getStyleName(),
                StyleDialog.WIDTH, source.getWidth(),
                StyleDialog.WIDTH_UNITS, source.getWidthUnit(),
                StyleDialog.HEIGHT, source.getHeight(),
                StyleDialog.HEIGHT_UNITS, source.getHeightUnit()));
        weightDialog.addCloseListener(actionId -> {
            if (Window.COMMIT_ACTION_ID.equals(actionId)) {
                Dashboard dashboard = getDashboard();
                DashboardLayout layout = dashboard.getVisualModel().findLayout(source.getUuid());
                layout.setStyleName(weightDialog.getLayoutStyleName());
                layout.setHeight(weightDialog.getLayoutHeight());
                layout.setHeightUnit(weightDialog.getLayoutHeightUnit());
                layout.setWidth(weightDialog.getLayoutWidth());
                layout.setWidthUnit(weightDialog.getLayoutWidthUnit());
                events.publish(new DashboardRefreshEvent(dashboard.getVisualModel(), source.getUuid()));
            }
        });
        ((AbstractDatasource) dashboardDs).setModified(true);
    }

    private String getAdditionalStyleName(CanvasLayout source) {
        String styleName = source.getStyleName();
        if (styleName != null) {
            styleName = styleName.replace(DashboardStyleConstants.DASHBOARD_SHADOW_BORDER, "");
            styleName = styleName.replace(DashboardStyleConstants.DASHBOARD_TREE_SELECTED, "");
            return styleName.trim();
        }
        return null;
    }

    @EventListener
    public void onRemoveLayout(WidgetRemovedEvent event) {
        DashboardLayout dashboardLayout = getDashboard().getVisualModel();
        dashboardLayout.removeChild(event.getSource().getUuid());
        events.publish(new DashboardRefreshEvent(dashboardLayout));
        ((AbstractDatasource) dashboardDs).setModified(true);
    }

    @EventListener
    public void widgetAddedToTreeEventListener(WidgetAddedEvent event) {
        dropLayoutTools.addComponent(event.getSource(), event.getParentLayoutUuid(), event.getLocation());
        ((AbstractDatasource) dashboardDs).setModified(true);
    }

    @EventListener
    public void onOpenWidgetEditor(WidgetEditEvent event) {
        Widget widget = event.getSource().getWidget();
        WidgetEdit editor = (WidgetEdit) openEditor(WidgetEdit.SCREEN_NAME, widget, DIALOG);
        editor.addCloseWithCommitListener(() -> {
            WidgetLayout widgetLayout = getDashboard().getWidgetLayout(widget.getId());
            widgetLayout.setWidget(editor.getItem());
            ((AbstractDatasource) dashboardDs).setModified(true);
            events.publish(new DashboardRefreshEvent(getDashboard().getVisualModel(), widget.getId()));
        });
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        //remove errors from widget frames
        errors.getAll().removeIf(error -> !"dashboard$dashboardEditFieldGroup1".equals(error.component.getParent().getId()));

        List<Widget> dashboardWidgets = dashboardDs.getItem().getWidgets();
        Map<String, Long> widgetsCount = dashboardWidgets.stream()
                .collect(Collectors.groupingBy(Widget::getWidgetId, Collectors.counting()));
        List<String> nonUniqueIds = widgetsCount.entrySet().stream()
                .filter(es -> es.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(toList());

        if (nonUniqueIds.size() > 0) {
            errors.add(null, formatMessage("uniqueWidgetId", String.join(",", nonUniqueIds)));
        }
    }

    private Map<String, Object> getDisplaySizeMap(ResponsiveArea responsiveArea) {
        Map<String, Object> map = new HashMap<>();
        map.put(XS.name(), responsiveArea.getXs() == null ? null : new Double(responsiveArea.getXs()));
        map.put(SM.name(), responsiveArea.getSm() == null ? null : new Double(responsiveArea.getSm()));
        map.put(MD.name(), responsiveArea.getMd() == null ? null : new Double(responsiveArea.getMd()));
        map.put(LG.name(), responsiveArea.getLg() == null ? null : new Double(responsiveArea.getLg()));
        return map;
    }

}
