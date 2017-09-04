/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.dashboardwidget;

import com.audimex.dashboard.entity.DashboardWidget;
import com.audimex.dashboard.entity.WidgetViewType;
import com.google.common.collect.ImmutableList;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.ScreensHelper;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.DsBuilder;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardWidgetEdit extends AbstractEditor<DashboardWidget> {
    WindowConfig windowConfig = AppBeans.get(WindowConfig.NAME);

    @Inject
    protected Datasource<DashboardWidget> dashboardWidgetDs;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private FieldGroup fieldGroup;

    @Inject
    protected ScreensHelper screensHelper;

    @Named("fieldGroup.widgetViewType")
    protected LookupField widgetViewTypeField;

    @Named("parametersTable.create")
    protected CreateAction parametersTableCreateAction;

    @Inject
    protected Button btnCreate;

    @Inject
    protected Metadata metadata;

    protected static final List<String> allFieldNames = ImmutableList.of(
            "frameId",
            "entityType",
            "report"
    );

    protected static final List<String> commonFieldNames = ImmutableList.of("frameId");

    protected static final List<String> listFieldNames = ImmutableList.of("entityType");

    protected static final List<String> chartFieldNames = ImmutableList.of("report");

    @Override
    protected void initNewItem(DashboardWidget item) {
        super.initNewItem(item);

        item.setWidgetViewType(WidgetViewType.COMMON);
    }

    @Override
    protected void postInit() {
        super.postInit();

        showComponents(getItem().getWidgetViewType());

        initFrameIdField();
        initWidgetViewTypeValueChangeListener();
        initCreateButtonActions();
        initEntityTypeField();
        initReportField();
    }

    private void initReportField() {
        if (fieldGroup.getFieldNN("report").getComponent() != null) return;

        FieldGroup.FieldConfig reportFieldConfig = fieldGroup.getField("report");
        LookupField lookupField = componentsFactory.createComponent(LookupField.class);
        lookupField.setDatasource(dashboardWidgetDs, reportFieldConfig.getProperty());
        reportFieldConfig.setComponent(lookupField);

        CollectionDatasource ds = new DsBuilder(getDsContext())
                .setJavaClass(com.haulmont.reports.entity.Report.class)
                .setAllowCommit(false)
                .setViewName("report.view")
                .setId("reportDs")
                .buildCollectionDatasource();

        ds.refresh();

        lookupField.setOptionsDatasource(ds);
    }

    protected void initFrameIdField() {
        if (fieldGroup.getFieldNN("frameId").getComponent() != null) return;

        fieldGroup.addCustomField("frameId", (datasource, propertyId) -> {
            LookupField lookupField = componentsFactory.createComponent(LookupField.class);
            Map<String, String> availableFrames = new HashMap<>();

            try {
                for (WindowInfo windowInfo : windowConfig.getWindows()) {
                    if (isApplicableScreen(windowInfo)) {
                        String screenCaption = screensHelper.getScreenCaption(windowInfo);
                        String screenName;
                        if (StringUtils.isNotBlank(screenCaption)) {
                            screenName = screenCaption +
                                    " (" +
                                    windowInfo.getId() +
                                    ")";
                        } else {
                            screenName = windowInfo.getId();
                        }
                        availableFrames.put(screenName,
                                windowInfo.getId());
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            lookupField.setOptionsMap(availableFrames);
            lookupField.setDatasource(datasource, propertyId);
            return lookupField;
        });
    }

    protected void initEntityTypeField() {
        if (fieldGroup.getFieldNN("entityTypeField").getComponent() != null) return;

        FieldGroup.FieldConfig entityTypeFieldConfig = fieldGroup.getField("entityTypeField");
        LookupField lookupField = componentsFactory.createComponent(LookupField.class);
        lookupField.setDatasource(dashboardWidgetDs, entityTypeFieldConfig.getProperty());
        entityTypeFieldConfig.setComponent(lookupField);

        Map<String, Object> metaClasses = new LinkedHashMap<>();
        metadata.getTools().getAllPersistentMetaClasses()
                .forEach(metaClass ->
                        metaClasses.put(metaClass.getName(), metaClass.getName())
                );
        lookupField.setOptionsMap(metaClasses);
    }

    protected void initWidgetViewTypeValueChangeListener() {
        widgetViewTypeField.addValueChangeListener(event -> {
            if (event.getValue() == null) {
                return;
            }

            showComponents((WidgetViewType) event.getValue());
        });
    }

    protected void initCreateButtonActions() {
        parametersTableCreateAction.setBeforeActionPerformedHandler(() -> {
            if (PersistenceHelper.isNew(getItem())) {
                showNotification(getMessage("notification.warning.pleaseSaveDashboard"),
                        NotificationType.TRAY);
                return false;
            }

            return true;
        });
    }

    protected void showComponents(WidgetViewType type) {
        switch (type) {
            case COMMON:
                showComponents(commonFieldNames);
                break;
            case LIST:
                showComponents(listFieldNames);
                break;
            case CHART:
                showComponents(chartFieldNames);
                break;
            default:
        }
    }

    protected void showComponents(List<String> componentNames) {
        allFieldNames.forEach(name -> {
            FieldGroup.FieldConfig field = fieldGroup.getField(name);

            if (field == null) {
                return;
            }

            if (componentNames.contains(name)) {
                showComponent(field, true);
            } else {
                showComponent(field, false);
            }
        });
    }

    protected void showComponent(FieldGroup.FieldConfig field, boolean isShowing) {
        field.setVisible(isShowing);
        field.setRequired(isShowing);
    }

    protected boolean isApplicableScreen(WindowInfo windowInfo) {
        if (windowInfo.getId().contains(".edit")) {
            return false;
        }

        if (windowInfo.getId().contains("filterSelect")) {
            return false;
        }

        if (windowInfo.getId().contains(".changePassword")) {
            return false;
        }

        if (windowInfo.getId().contains("filterEditor")) {
            return false;
        }

        if (windowInfo.getId().contains("loginWindow")) {
            return false;
        }

        if (windowInfo.getId().contains("mainWindow")) {
            return false;
        }

        return true;
    }
}