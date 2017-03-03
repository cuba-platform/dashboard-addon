/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.screens;

import com.audimex.dashboard.web.layouts.HasGridSpan;
import com.audimex.dashboard.web.layouts.HasWeight;
import com.audimex.dashboard.web.utils.LayoutUtils;
import com.audimex.dashboard.web.utils.TreeUtils;
import com.audimex.dashboard.web.widgets.GridCell;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.HBoxLayout;
import com.haulmont.cuba.gui.components.actions.BaseAction;
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
    protected com.haulmont.cuba.gui.components.Button okButton;

    @Inject
    protected com.haulmont.cuba.gui.components.Button cancelButton;

    Component widget = null;
    Tree tree = null;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        widget = (Component) params.get("widget");
        tree = (Tree) params.get("tree");

        Layout layout = (Layout) WebComponentsHelper.unwrap(sliderLayout);

        if (widget.getParent() instanceof AbstractOrderedLayout) {
            weightSlider = new Slider();
            weightSlider.setImmediate(true);
            weightSlider.setWidth("300px");
            weightSlider.setCaption(getMessage("weightInLayout"));
            weightSlider.setMin(1);
            weightSlider.setMax(10);
            weightSlider.setResolution(0);
            weightSlider.setValue((double) ((HasWeight) widget).getWeight());
            layout.addComponent(weightSlider);
            weightSlider.focus();
        }

        if (widget.getParent() instanceof GridLayout) {
            colSpanSlider = new Slider();
            colSpanSlider.setImmediate(true);
            colSpanSlider.setCaption(getMessage("columnSpan"));
            colSpanSlider.setWidth("100%");
            colSpanSlider.setMin(1);

            colSpanSlider.setMax(LayoutUtils.availableColumns(
                    (GridLayout) widget.getParent(),
                    LayoutUtils.getWidgetCell(tree, widget))
            );

            colSpanSlider.setResolution(0);
            colSpanSlider.setValue((double) ((HasGridSpan) widget).getColSpan());
            layout.addComponent(colSpanSlider);
            colSpanSlider.focus();

            rowSpanSlider = new Slider();
            rowSpanSlider.setImmediate(true);
            rowSpanSlider.setCaption(getMessage("rowSpan"));
            rowSpanSlider.setWidth("100%");
            rowSpanSlider.setMin(1);
            if (widget.getParent() instanceof GridLayout) {
                rowSpanSlider.setMax(LayoutUtils.availableRows(
                        (GridLayout) widget.getParent(),
                        LayoutUtils.getWidgetCell(tree, widget))
                );
            }
            rowSpanSlider.setResolution(0);
            rowSpanSlider.setValue((double) ((HasGridSpan) widget).getRowSpan());

            layout.addComponent(rowSpanSlider);
        }
    }

    public void cancel() {
        close("");
    }

    public void save() {
        if (widget.getParent() instanceof AbstractOrderedLayout) {
            ((HasWeight) widget).setWeight(weightSlider.getValue().intValue());
        }
        if (widget.getParent() instanceof GridLayout) {
            GridCell gridCell = LayoutUtils.getWidgetCell(tree, widget);
            int availableRowSpan = LayoutUtils.availableRows((GridLayout) widget.getParent(), gridCell);
            int availableColSpan = LayoutUtils.availableColumns((GridLayout) widget.getParent(), gridCell);
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

                GridCell gridCellComponent = LayoutUtils.getWidgetCell(tree, widget);
                TreeUtils.markGridCells(tree,
                        (GridLayout) widget.getParent(),
                        gridCellComponent.getRow(),
                        gridCellComponent.getColumn(),
                        rowSpan,
                        colSpan);
            } else {
                showNotification(getMessage("notValidated"), NotificationType.ERROR);
            }
        }
        close("");
    }
}