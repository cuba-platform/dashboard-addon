/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.dashboardwidget;

import com.audimex.dashboard.entity.DashboardWidget;
import com.audimex.dashboard.entity.WidgetParameter;
import com.audimex.dashboard.entity.WidgetParameterType;
import com.audimex.dashboard.entity.WidgetViewType;
import com.google.common.collect.ImmutableList;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.ScreensHelper;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.DsBuilder;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.reports.entity.ParameterType;
import com.haulmont.reports.entity.Report;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.FileNotFoundException;
import java.util.*;

import static com.audimex.dashboard.entity.WidgetViewType.CHART;

public class DashboardWidgetEdit extends AbstractEditor<DashboardWidget> {
    WindowConfig windowConfig = AppBeans.get(WindowConfig.NAME);

    @Inject
    protected Datasource<DashboardWidget> dashboardWidgetDs;

    @Inject
    protected CollectionDatasource<WidgetParameter, UUID> widgetParametersDs;

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

    protected List<Field> allFieldNames;

    protected List<Field> commonFieldNames;

    protected List<Field> listFieldNames;

    protected List<Field> chartFieldNames;

    protected LookupField reportLookupField;

    protected LookupField lookupEntityTypeField;

    protected LookupField lookupFrameIdField;

    @Override
    protected void initNewItem(DashboardWidget item) {
        super.initNewItem(item);

        item.setWidgetViewType(WidgetViewType.COMMON);
        item.setIsTemplate(true);
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initFieldList();
        initCreateButtonActions();
    }

    @Override
    protected void postInit() {
        super.postInit();
        showComponents(getItem().getWidgetViewType());
        initWidgetViewTypeValueChangeListener();

        reportLookupField.addValueChangeListener(e -> {
            clearParameterDs();
            Report report = (Report) e.getValue();
            createReportParameters(report);
        });
    }

    protected void initFieldList() {
        lookupFrameIdField = initFrameIdField();
        reportLookupField = initReportField();
        lookupEntityTypeField = initEntityTypeField();

        allFieldNames = ImmutableList.of(
                lookupFrameIdField,
                reportLookupField
        );

        commonFieldNames = ImmutableList.of(lookupFrameIdField);
        listFieldNames = ImmutableList.of(lookupFrameIdField);
        chartFieldNames = ImmutableList.of(reportLookupField);
    }

    protected LookupField initFrameIdField() {
        FieldGroup.FieldConfig frameIdFieldConfig = fieldGroup.getField("frameId");
        LookupField lookupField = componentsFactory.createComponent(LookupField.class);
        lookupField.setDatasource(dashboardWidgetDs, frameIdFieldConfig.getProperty());
        frameIdFieldConfig.setComponent(lookupField);
        setDefaultApplicableScreens(lookupField);
        return lookupField;
    }

    protected void setDefaultApplicableScreens(LookupField lookupEntityTypeField) {
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

        lookupEntityTypeField.setOptionsMap(availableFrames);
    }


    protected LookupField initReportField() {
        FieldGroup.FieldConfig reportFieldConfig = fieldGroup.getField("report");
        LookupField reportLookupField = componentsFactory.createComponent(LookupField.class);
        reportLookupField.setDatasource(dashboardWidgetDs, reportFieldConfig.getProperty());
        reportFieldConfig.setComponent(reportLookupField);

        CollectionDatasource ds = new DsBuilder(getDsContext())
                .setJavaClass(Report.class)
                .setAllowCommit(false)
                .setViewName("report.edit")
                .setId("reportDs")
                .buildCollectionDatasource();

        ds.refresh();

        reportLookupField.setOptionsDatasource(ds);

        return reportLookupField;
    }

    protected void createReportParameters(Report report) {
        if (report != null && report.getInputParameters() != null) {
            report.getInputParameters().forEach(param -> {
                String name = param.getAlias();
                String alias = param.getAlias();
                String metaClass = param.getEntityMetaClass();
                WidgetParameterType type = WidgetParameterType.UNDEFINED;
                ParameterType parameterType = param.getType();
                switch (parameterType) {
                    case DATE: case TIME: case DATETIME:
                        type = WidgetParameterType.DATE;
                        break;
                    case TEXT:
                        type = WidgetParameterType.STRING;
                        break;
                    case BOOLEAN:
                        type = WidgetParameterType.BOOLEAN;
                        break;
                    case NUMERIC:
                        type = WidgetParameterType.DECIMAL;
                        break;
                       /*case ENUMERATION:
                            break;*/
                    case ENTITY:
                        type = WidgetParameterType.ENTITY;
                        break;
                    case ENTITY_LIST:
                        type = WidgetParameterType.LIST_ENTITY;
                        break;
                    default:
                }
                WidgetParameter wp = createWidgetParameter(name, alias, metaClass, type);
                widgetParametersDs.addItem(wp);
            });
        }
    }

    protected WidgetParameter createWidgetParameter(String name, String alias, String metaClass,
                                                    WidgetParameterType parameterType) {
        WidgetParameter wp = metadata.create(WidgetParameter.class);
        wp.setName(name);
        wp.setAlias(alias);
        wp.setParameterType(parameterType);
        wp.setDashboardWidget(dashboardWidgetDs.getItem());
        wp.getReferenceToEntity().setMetaClassName(metaClass);
        wp.getReferenceToEntity().setViewName(View.LOCAL);
        return wp;
    }

    protected LookupField initEntityTypeField() {
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

        lookupField.addValueChangeListener(event -> {
            String metaClass = (String) event.getValue();
            if (metaClass != null) {
                Class mClass = metadata.getSession().getClassNN(metaClass).getJavaClass();
                Map<String, Object> applicableScreens = screensHelper.getAvailableScreens(mClass);
                lookupFrameIdField.setOptionsMap(applicableScreens);
            } else {
                setDefaultApplicableScreens(lookupFrameIdField);
            }
        });

        return lookupField;
    }

    protected void initWidgetViewTypeValueChangeListener() {
        widgetViewTypeField.addValueChangeListener(event -> {
            if (WidgetViewType.CHART.equals(dashboardWidgetDs.getItem().getWidgetViewType()) &&
                    dashboardWidgetDs.getItem().getReport() != null) {
                createReportParameters(dashboardWidgetDs.getItem().getReport());
            } else {
                clearParameterDs();
            }

            showComponents((WidgetViewType) event.getValue());
        });
    }

    protected void clearParameterDs() {
        Collection<WidgetParameter> parameters = new ArrayList<>(widgetParametersDs.getItems());
        parameters.forEach(widgetParametersDs::removeItem);
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
        lookupEntityTypeField.setRequired(false);
        switch (type) {
            case COMMON:
                showComponents(commonFieldNames);
                break;
            case LIST:
                showComponents(listFieldNames);
                lookupEntityTypeField.setRequired(true);
                break;
            case CHART:
                showComponents(chartFieldNames);
                break;
            default:
        }
    }

    protected void showComponents(List<Field> componentNames) {
        allFieldNames.forEach(field -> {
            if (componentNames.contains(field)) {
                showComponent(field, true);
            } else {
                showComponent(field, false);
            }
        });
    }

    protected void showComponent(Field field, boolean isShowing) {
        field.setVisible(isShowing);
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

    @Override
    protected boolean preCommit() {
        if (CHART.equals(getItem().getWidgetViewType())) {
            getItem().setFrameId("amxd$Empty.frame");
        }
        return super.preCommit();
    }
}