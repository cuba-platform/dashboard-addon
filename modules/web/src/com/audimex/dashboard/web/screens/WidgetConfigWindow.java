/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.screens;

import com.audimex.dashboard.web.layouts.DashboardGridLayout;
import com.audimex.dashboard.web.layouts.HasGridSpan;
import com.audimex.dashboard.web.layouts.HasWeight;
import com.audimex.dashboard.web.tools.DashboardTools;
import com.audimex.dashboard.web.widgets.GridCell;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.HBoxLayout;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.vaadin.ui.*;

import javax.inject.Inject;
import java.util.Map;

public class WidgetConfigWindow extends AbstractWindow {
    protected Slider weightSlider = null;
    protected Slider colSpanSlider = null;
    protected Slider rowSpanSlider = null;

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

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        Layout layout = (Layout) WebComponentsHelper.unwrap(sliderLayout);

        if (widget.getParent() instanceof AbstractOrderedLayout) {
            int weight = ((HasWeight) widget).getWeight();
            weightSlider = new Slider();
            weightSlider.setImmediate(true);
            weightSlider.setWidth("300px");
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

            colSpanSlider.setMax(dashboardTools.availableColumns(
                    (GridLayout) widget.getParent(),
                    dashboardTools.getWidgetCell(tree, widget))
            );

            colSpanSlider.addValueChangeListener(event ->
                    leftLabel.setValue(String.format(
                            getMessage("dashboard.columnSpan"),
                            colSpanSlider.getValue().intValue())
                    )
            );
            leftLabel.setValue(String.format(getMessage("dashboard.columnSpan"), ((HasGridSpan) widget).getColSpan()));
            colSpanSlider.setResolution(0);
            colSpanSlider.setValue((double) ((HasGridSpan) widget).getColSpan());
            layout.addComponent(colSpanSlider);
            colSpanSlider.focus();

            rowSpanSlider = new Slider();
            rowSpanSlider.setImmediate(true);
            rowSpanSlider.setWidth("100%");
            rowSpanSlider.setMin(1);
            if (widget.getParent() instanceof GridLayout) {
                rowSpanSlider.setMax(dashboardTools.availableRows(
                        (GridLayout) widget.getParent(),
                        dashboardTools.getWidgetCell(tree, widget))
                );
            }
            rowSpanSlider.addValueChangeListener(event ->
                    rightLabel.setValue(String.format(
                            getMessage("dashboard.rowSpan"),
                            rowSpanSlider.getValue().intValue())
                    )
            );
            rightLabel.setValue(String.format(getMessage("dashboard.rowSpan"), ((HasGridSpan) widget).getRowSpan()));
            rowSpanSlider.setResolution(0);
            rowSpanSlider.setValue((double) ((HasGridSpan) widget).getRowSpan());

            layout.addComponent(rowSpanSlider);
        }
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