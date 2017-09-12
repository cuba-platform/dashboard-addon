/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.screens;

import com.audimex.dashboard.entity.DashboardWidget;
import com.audimex.dashboard.entity.ReferenceToEntity;
import com.audimex.dashboard.entity.WidgetParameter;
import com.audimex.dashboard.web.layouts.DashboardGridLayout;
import com.audimex.dashboard.web.layouts.HasGridSpan;
import com.audimex.dashboard.web.layouts.HasWeight;
import com.audimex.dashboard.web.screens.events.ChangeParameterEvent;
import com.audimex.dashboard.web.screens.frames.LookupFrame;
import com.audimex.dashboard.web.tools.DashboardTools;
import com.audimex.dashboard.web.tools.ParameterTools;
import com.audimex.dashboard.web.widgets.FramePanel;
import com.audimex.dashboard.web.widgets.GridCell;
import com.google.common.collect.ImmutableList;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DsBuilder;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.App;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.vaadin.ui.*;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Tree;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;

public class WidgetConfigWindow extends AbstractWindow {
    protected Slider weightSlider = null;
    protected Slider colSpanSlider = null;
    protected Slider rowSpanSlider = null;

    public static final String VALUE = "VALUE";
    public static final String MAPPED_ALIAS = "MAPPED_ALIAS";

    @Inject
    protected VBoxLayout parametersBox;

    @Inject
    protected HBoxLayout sliderLayout;

    @Inject
    protected Button okButton;

    @Inject
    protected Button cancelButton;

    @WindowParam(name = "widget")
    protected Component widget;

    @WindowParam(name = "tree")
    protected Tree tree;

    @Inject
    protected DashboardTools dashboardTools;

    @Inject
    private Label leftLabel;

    @Inject
    private Label rightLabel;

    @Inject
    protected ComponentsFactory componentsFactory;

    @Inject
    protected Metadata metadata;

    @Inject
    protected ParameterTools parameterTools;

    protected static final List<Boolean> BOOLEAN_LIST = ImmutableList.of(true, false);

    protected Map<WidgetParameter, ChangeParameterEvent> changeParameterEvents = new HashMap<>();

    protected WindowManager windowManager = App.getInstance().getWindowManager();
    protected WindowConfig windowConfig = AppBeans.get(WindowConfig.class);
    protected WindowInfo lookupWindowInfo;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        preInit();

        lookupWindowInfo = windowConfig.getWindowInfo(LookupFrame.SCREEN_ID);

        Layout layout = (Layout) WebComponentsHelper.unwrap(sliderLayout);

        if (widget.getParent() instanceof AbstractOrderedLayout) {
            int weight = ((HasWeight) widget).getWeight();
            weightSlider = new Slider();
            weightSlider.setImmediate(true);
            weightSlider.setWidth("480px");
            weightSlider.setMin(1);
            weightSlider.setMax(10);
            weightSlider.setResolution(0);
            weightSlider.setValue((double) weight);
            weightSlider.addValueChangeListener(event ->
                    leftLabel.setValue(String.format(
                            getMessage("dashboard.weightInLayout"),
                            weightSlider.getValue().intValue())
                    )
            );
            leftLabel.setValue(String.format(getMessage("dashboard.weightInLayout"), weight));
            layout.addComponent(weightSlider);
            weightSlider.focus();
        }

        if (widget.getParent() instanceof GridLayout) {
            colSpanSlider = new Slider();
            colSpanSlider.setImmediate(true);
            colSpanSlider.setWidth("100%");
            colSpanSlider.setMin(1);
            int maxColSpan = dashboardTools.availableColumns(
                    (GridLayout) widget.getParent(),
                    dashboardTools.getWidgetCell(tree, widget)
            );
            int maxRowSpan = dashboardTools.availableRows(
                    (GridLayout) widget.getParent(),
                    dashboardTools.getWidgetCell(tree, widget)
            );

            colSpanSlider.setMax(maxColSpan);

            colSpanSlider.addValueChangeListener(event ->
                    leftLabel.setValue(String.format(
                            getMessage("dashboard.columnSpan"),
                            colSpanSlider.getValue().intValue(),
                            maxColSpan)
                    )
            );
            leftLabel.setValue(String.format(
                    getMessage("dashboard.columnSpan"),
                    ((HasGridSpan) widget).getColSpan(),
                    maxColSpan
            ));
            colSpanSlider.setResolution(0);
            colSpanSlider.setValue((double) ((HasGridSpan) widget).getColSpan());
            layout.addComponent(colSpanSlider);
            colSpanSlider.focus();

            rowSpanSlider = new Slider();
            rowSpanSlider.setImmediate(true);
            rowSpanSlider.setWidth("100%");
            rowSpanSlider.setMin(1);
            if (widget.getParent() instanceof GridLayout) {
                rowSpanSlider.setMax(maxRowSpan);
            }
            rowSpanSlider.addValueChangeListener(event ->
                    rightLabel.setValue(String.format(
                            getMessage("dashboard.rowSpan"),
                            rowSpanSlider.getValue().intValue(),
                            maxRowSpan))
            );
            rightLabel.setValue(String.format(getMessage(
                    "dashboard.rowSpan"),
                    ((HasGridSpan) widget).getRowSpan(),
                    maxRowSpan
            ));
            rowSpanSlider.setResolution(0);
            rowSpanSlider.setValue((double) ((HasGridSpan) widget).getRowSpan());

            layout.addComponent(rowSpanSlider);
        }

        if (widget instanceof FramePanel) {
            DashboardWidget dashboardWidget = ((FramePanel) widget).getWidget();
            parametersInit(dashboardWidget);
        }
    }

    protected void preInit() {

    }

    protected void parametersInit(DashboardWidget dashboardWidget) {
        dashboardWidget.getDashboardLinks().forEach(link ->
                link.getDashboardParameters().stream()
                        .sorted(Comparator.comparing(WidgetParameter::getName))
                        .forEach(param -> {
                            com.haulmont.cuba.gui.components.Component component = createComponent(param);
                            if (component != null) {
                                parametersBox.add(component);
                            }
                        })
        );
    }

    protected com.haulmont.cuba.gui.components.Component createComponent(WidgetParameter parameter) {
        com.haulmont.cuba.gui.components.Component component;
        HBoxLayout parameterArea = componentsFactory.createComponent(HBoxLayout.class);
        parameterArea.setWidth("100%");
        parameterArea.setSpacing(true);

        switch (parameter.getParameterType()) {
            case ENTITY:
                component = createEntityField(parameter);
                break;
            case LIST_ENTITY:
                component = createListEntityField(parameter);
                break;
            case DATE:
                component = createDateField(parameter);
                break;
            case INTEGER:
                component = createIntegerField(parameter);
                break;
            case STRING:
                component = createStringField(parameter);
                break;
            case DECIMAL:
                component = createDecimalField(parameter);
                break;
            case BOOLEAN:
                component = createBooleanField(parameter);
                break;
            case LONG:
                component = createLongField(parameter);
                break;
            default:
                component = null;
        }

        Label parameterNameField = componentsFactory.createComponent(Label.class);
        parameterNameField.setWidth("120px");
        parameterNameField.setAlignment(Alignment.MIDDLE_CENTER);
        parameterNameField.setValue(
                String.format("%s (%s)", parameter.getName(), parameter.getAlias())
        );

        TextField mappedAliasField = createMappedAliasField(parameter);

        parameterArea.add(parameterNameField);
        parameterArea.add(mappedAliasField);
        parameterArea.add(component);

        return parameterArea;
    }

    protected void addChangeEvent(WidgetParameter parameter, String eventType, Object value) {
        if (changeParameterEvents.containsKey(parameter)) {
            ChangeParameterEvent event = changeParameterEvents.get(parameter);
            event.addChangeValue(eventType, value);
            changeParameterEvents.replace(parameter, event);
        } else {
            ChangeParameterEvent event = new ChangeParameterEvent();
            event.addChangeValue(eventType, value);
            changeParameterEvents.put(parameter, event);
        }
    }
    public void cancel() {
        close(CLOSE_ACTION_ID);
    }

    public void save() {
        widgetParametersSaveFunction();

        if (widget.getParent() instanceof AbstractOrderedLayout) {
            ((HasWeight) widget).setWeight(weightSlider.getValue().intValue());
        }
        if (widget.getParent() instanceof GridLayout) {
            GridCell gridCell = dashboardTools.getWidgetCell(tree, widget);
            int availableRowSpan = dashboardTools.availableRows((GridLayout) widget.getParent(), gridCell);
            int availableColSpan = dashboardTools.availableColumns((GridLayout) widget.getParent(), gridCell);
            int colspan = colSpanSlider.getValue().intValue();
            int rowspan = rowSpanSlider.getValue().intValue();
            boolean isValid = true;

            if (availableRowSpan >= 0
                    && availableRowSpan >= rowspan
                    && availableColSpan >= colspan
                    && availableColSpan >= 0) {
                for (int row = gridCell.getRow(); row < gridCell.getRow() + rowspan; row++) {
                    for (int column = gridCell.getColumn(); column < gridCell.getColumn() + colspan; column++) {
                        Component gridComponent = ((GridLayout) widget.getParent()).getComponent(column, row);
                        if (!(gridComponent instanceof GridCell) && !gridComponent.equals(widget)) {
                            isValid = false;
                        }
                    }
                }
            } else {
                isValid = false;
            }

            if (isValid) {
                int colSpan = colSpanSlider.getValue().intValue();
                int rowSpan = rowSpanSlider.getValue().intValue();

                ((HasGridSpan) widget).setColSpan(colSpan);
                ((HasGridSpan) widget).setRowSpan(rowSpan);

                GridCell gridCellComponent = dashboardTools.getWidgetCell(tree, widget);
                dashboardTools.markGridCells(tree,
                        (DashboardGridLayout) widget.getParent().getParent(),
                        gridCellComponent.getRow(),
                        gridCellComponent.getColumn(),
                        rowSpan,
                        colSpan);
            } else {
                showNotification(getMessage("dashboard.notValidated"), NotificationType.ERROR);
            }
        }
        tree.focus();
        close(COMMIT_ACTION_ID);
    }

    protected void widgetParametersSaveFunction() {
        if (changeParameterEvents.size() > 0) {
            changeParameterEvents.forEach((parameter, event) -> {
                event.getChangeValues().forEach((key, object) -> {
                    switch (key) {
                        case VALUE:
                            parameter.setValue(object);
                            break;
                        case MAPPED_ALIAS:
                            parameter.setMappedAlias((String) object);
                            break;
                        default:
                    }
                });
            });
        }
    }

    protected LookupField createEntityField(WidgetParameter parameter) {
        LookupField entityField = componentsFactory.createComponent(LookupField.class);
        entityField.setWidth("100%");
        entityField.setInputPrompt(getMessage("message.value"));

        ReferenceToEntity reference = parameter.getReferenceToEntity();

        MetaClass metaClass = metadata.getSession()
                .getClassNN(reference.getMetaClassName());
        CollectionDatasource ds = new DsBuilder(getDsContext())
                .setJavaClass(metaClass.getJavaClass())
                .setAllowCommit(false)
                .setViewName(reference.getViewName())
                .setId(parameter.getName() + "Ds")
                .buildCollectionDatasource();

        ds.refresh();

        entityField.setOptionsDatasource(ds);
        entityField.setValue(parameterTools.getWidgetLinkParameterValue(parameter));
        entityField.addValueChangeListener(event -> {
            addChangeEvent(parameter, VALUE, event.getValue());
        });
        return entityField;
    }

    protected LookupFrame createListEntityField(WidgetParameter parameter) {
        LookupFrame lookupFrame = (LookupFrame) windowManager.openFrame(
                this.getFrame(),
                null,
                lookupWindowInfo,
                ParamsMap.of(LookupFrame.WIDGET_PARAMETER, parameter)
        );
        frame.setParent(this);
        lookupFrame.setValueChangeListener(event ->
                addChangeEvent(parameter, VALUE, lookupFrame.getValue())
        );
        return lookupFrame;
    }

    protected DateField createDateField(WidgetParameter parameter) {
        DateField dateField = componentsFactory.createComponent(DateField.class);
        dateField.setWidth("100%");
        dateField.setDateFormat("dd.MM.yyyy");
        dateField.setValue(parameter.getDateValue());
        dateField.addValueChangeListener(event -> {
            Date date = (Date) event.getValue();
            addChangeEvent(parameter, VALUE, date);
        });
        return dateField;
    }

    protected TextField createIntegerField(WidgetParameter parameter) {
        TextField integerField = componentsFactory.createComponent(TextField.class);
        integerField.setWidth("100%");
        integerField.setInputPrompt(getMessage("message.value"));
        integerField.setValue(parameter.getIntegerValue());
        integerField.addValueChangeListener(event -> {
            Integer intValue = event.getValue() != null ? Integer.parseInt((String) event.getValue()) : null;
            addChangeEvent(parameter, VALUE, intValue);
        });
        return integerField;
    }

    protected TextField createStringField(WidgetParameter parameter) {
        TextField stringField = componentsFactory.createComponent(TextField.class);
        stringField.setWidth("100%");
        stringField.setInputPrompt(getMessage("message.value"));
        stringField.setValue(parameter.getStringValue());
        stringField.addValueChangeListener(event -> {
            String strValue = (String) event.getValue();
            addChangeEvent(parameter, VALUE, strValue);
        });
        return stringField;
    }

    protected TextField createDecimalField(WidgetParameter parameter) {
        TextField decimalField = componentsFactory.createComponent(TextField.class);
        decimalField.setWidth("100%");
        decimalField.setInputPrompt(getMessage("message.value"));
        decimalField.setValue(parameter.getDecimalValue());
        decimalField.addValueChangeListener(event -> {
            BigDecimal decValue = event.getValue() != null ? BigDecimal.valueOf(
                    Long.parseLong((String) event.getValue())
            ) : null;
            addChangeEvent(parameter, VALUE, decValue);
        });
        return decimalField;
    }

    protected LookupField createBooleanField(WidgetParameter parameter) {
        LookupField booleanField = componentsFactory.createComponent(LookupField.class);
        booleanField.setWidth("100%");
        booleanField.setInputPrompt(getMessage("message.value"));
        booleanField.setOptionsList(BOOLEAN_LIST);
        booleanField.setValue(parameter.getBoolValue());
        booleanField.addValueChangeListener(event -> {
            Boolean booleanDate = (Boolean) event.getValue();
            addChangeEvent(parameter, VALUE, booleanDate);
        });
        return booleanField;
    }

    protected TextField createLongField(WidgetParameter parameter) {
        TextField longField = componentsFactory.createComponent(TextField.class);
        longField.setWidth("100%");
        longField.setInputPrompt(getMessage("message.value"));
        longField.setValue(parameter.getLongValue());
        longField.addValueChangeListener(event -> {
            Long longValue = event.getValue() != null ? Long.parseLong((String) event.getValue()) : null;
            addChangeEvent(parameter, VALUE, longValue);
        });
        return longField;
    }


    protected TextField createMappedAliasField(WidgetParameter parameter) {
        TextField mappedAliasField = componentsFactory.createComponent(TextField.class);
        mappedAliasField.setWidth("100%");
        mappedAliasField.setInputPrompt(getMessage("message.mappedValue"));
        mappedAliasField.setValue(parameter.getMappedAlias());
        mappedAliasField.addValueChangeListener(event -> {
            String strValue = (String) event.getValue();
            addChangeEvent(parameter, MAPPED_ALIAS, strValue);
        });
        return mappedAliasField;
    }
}