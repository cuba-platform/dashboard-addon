/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.frames.editor;

import com.haulmont.addon.dashboard.entity.PersistentDashboard;
import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.model.visualmodel.DashboardLayout;
import com.haulmont.addon.dashboard.model.visualmodel.RootLayout;
import com.haulmont.addon.dashboard.web.DashboardException;
import com.haulmont.addon.dashboard.web.dashboard.assistant.DashboardViewAssistant;
import com.haulmont.addon.dashboard.web.dashboard.converter.JsonConverter;
import com.haulmont.addon.dashboard.web.dashboard.events.DashboardRefreshEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.WidgetAddedEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.WidgetMovedEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.canvas.WeightChangedEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.canvas.WidgetRemovedEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.widget.WidgetEditEvent;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasEditorFrame;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.palette.PaletteFrame;
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
import com.haulmont.cuba.gui.export.ByteArrayDataProvider;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.AbstractApplicationContext;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame.DASHBOARD;
import static com.haulmont.addon.dashboard.web.parameter.ParameterBrowse.PARAMETERS;
import static com.haulmont.cuba.gui.WindowManager.OpenType.DIALOG;
import static com.haulmont.cuba.gui.WindowManager.OpenType.THIS_TAB;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.StringUtils.isNotBlank;

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
        dropLayoutTools = new DropLayoutTools(this, getDashboard(), modelConverter);
        initParametersFrame();
        initPaletteFrame();
        initCanvasFrame();
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
    }

    public void cancel() {
        close("close", true);
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
    }

    @EventListener
    public void onWeightChanged(WeightChangedEvent event) {
        CanvasLayout source = event.getSource();

        WeightDialog weightDialog = (WeightDialog) openWindow(WeightDialog.SCREEN_NAME, DIALOG, ParamsMap.of(
                WeightDialog.WEIGHT, source.getWeight()));
        weightDialog.addCloseListener(actionId -> {
            if (Window.COMMIT_ACTION_ID.equals(actionId)) {
                Dashboard dashboard = getDashboard();
                DashboardLayout layout = dashboard.getVisualModel().findLayout(source.getUuid());
                layout.setWeight(weightDialog.getWeight());
                events.publish(new DashboardRefreshEvent(dashboard.getVisualModel(), source.getUuid()));
            }
        });
    }

    @EventListener
    public void onRemoveLayout(WidgetRemovedEvent event) {
        DashboardLayout dashboardLayout = getDashboard().getVisualModel();
        dashboardLayout.removeChild(event.getSource().getUuid());
        events.publish(new DashboardRefreshEvent(dashboardLayout));
    }

    @EventListener
    public void widgetAddedToTreeEventListener(WidgetAddedEvent event) {
        dropLayoutTools.addComponent(event.getSource(), event.getParentLayoutUuid(), event.getLocation());
    }

    @EventListener
    public void onOpenWidgetEditor(WidgetEditEvent event) {
        Widget widget = event.getSource();
        WidgetEdit editor = (WidgetEdit) openEditor(WidgetEdit.SCREEN_NAME, widget, DIALOG);
        editor.addCloseWithCommitListener(() -> {
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

}
