/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.screens;

import com.audimex.dashboard.entity.DashboardWidget;
import com.audimex.dashboard.entity.WidgetParameter;
import com.audimex.dashboard.web.layouts.DashboardGridLayout;
import com.audimex.dashboard.web.layouts.HasGridSpan;
import com.audimex.dashboard.web.layouts.HasWeight;
import com.audimex.dashboard.web.tools.DashboardTools;
import com.audimex.dashboard.web.widgets.FramePanel;
import com.audimex.dashboard.web.widgets.GridCell;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.vaadin.ui.*;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Tree;

import javax.inject.Inject;
import java.util.Map;

public class WidgetConfigWindow extends AbstractWindow {
    protected Slider weightSlider = null;
    protected Slider colSpanSlider = null;
    protected Slider rowSpanSlider = null;

    @Inject
    protected HBoxLayout parametersBox;

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

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

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
            parametersInit(((FramePanel) widget).getWidget());
        }
    }

    protected void parametersInit(DashboardWidget dashboardWidget) {
        dashboardWidget.getDashboardLinks().forEach(link ->
                link.getDashboardParameters().forEach(param -> {
                    com.haulmont.cuba.gui.components.Component component = createComponent(param);
                    parametersBox.add(component);
                })
        );
    }

    protected com.haulmont.cuba.gui.components.Component createComponent(WidgetParameter parameter) {
        com.haulmont.cuba.gui.components.Component component;
        switch (parameter.getParameterType()) {
            case INTEGER:
                TextField textFieldInteger =
                        componentsFactory.createComponent(TextField.class);
                textFieldInteger.setWidth("100%");
                //textFieldInteger.setValue(entity.getDefaultIntegerValue());
                textFieldInteger.setEditable(false);
                component = textFieldInteger;
                break;
            case LIST_ENTITY:
                LookupField lookupField = componentsFactory.createComponent(LookupField.class);
                //lookupField.setOptionsDatasource(riskModelParamOptionDs);
                lookupField.setWidth("100%");
                //lookupField.setValue(entity.getDefaultListItemValue());
                lookupField.setEditable(false);
                component = lookupField;

/*                DataManager dataManager = AppBeans.get(DataManager.class);
                Metadata metadata = AppBeans.get(Metadata.class);
                MetaClass metaClass = metadata.getSession().getClassNN("sec$User");

                MetadataTools tools = AppBeans.get(MetadataTools.class);

                LoadContext<DashboardWidget> ctx = LoadContext.create(DashboardWidget.class)
                        .setQuery(LoadContext.createQuery("select dw from amxd$DashboardWidget dw"))
                        .setView("dashboardWidget-view");

                LoadContext ctx2 = LoadContext.create(metaClass.getJavaClass());

                dataManager.load(ctx2);
                //metadata.getSession().getClassNN("sec$User");*/
                break;
            case DECIMAL:
                TextField textFieldDecimal =
                        componentsFactory.createComponent(TextField.class);
                textFieldDecimal.setWidth("100%");
                //textFieldDecimal.setValue(entity.getDefaultDecimalValue());
                textFieldDecimal.setEditable(false);
                component = textFieldDecimal;
                break;
            case DATE:
                DateField dateField =
                        componentsFactory.createComponent(DateField.class);
                dateField.setWidth("100%");
                dateField.setDateFormat("dd.MM.yyyy");
                //dateField.setValue(entity.getDefaultDateValue());
                dateField.setEditable(false);
                component = dateField;
                break;
            case ENTITY:
                //component = new com.haulmont.cuba.gui.components.Table.PlainTextCell(parameter.getInstanceName());
                TextField textField =
                        componentsFactory.createComponent(TextField.class);
                component = textField;
/*
                component = new com.haulmont.cuba.gui.components.Table.PlainTextCell(entity.getDefaultAuditObjectValue() != null ?
                        entity.getDefaultAuditObjectValue().getInstanceName() : "");
*/
                break;
            default:
                component = componentsFactory.createComponent(TextField.class);
        }

        return component;
    }


    public void cancel() {
        close(CLOSE_ACTION_ID);
    }

    public void save() {
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
        close(CLOSE_ACTION_ID);
    }
}