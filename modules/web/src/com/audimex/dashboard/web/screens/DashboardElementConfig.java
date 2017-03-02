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
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;

import javax.inject.Inject;
import java.util.Map;

public class DashboardElementConfig extends AbstractWindow {
    private Slider weightSlider = null;
    private Slider colSpanSlider = null;
    private Slider rowSpanSlider = null;

    @Inject
    private VBoxLayout mainLayout;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        Component widget = (Component) params.get("widget");
        Tree tree = (Tree) params.get("tree");

        Layout layout = (Layout) WebComponentsHelper.unwrap(mainLayout);
        layout.setWidthUndefined();

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
        }

        HorizontalLayout spanLayout = new HorizontalLayout();
        spanLayout.setWidth("300px");
        spanLayout.setSpacing(true);

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
            spanLayout.addComponent(colSpanSlider);

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

            spanLayout.addComponent(rowSpanSlider);
            layout.addComponent(spanLayout);
        }

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setSpacing(true);
        Button okButton = new Button(getMessage("ok"), FontAwesome.CHECK);

        okButton.addClickListener((Button.ClickListener) event -> {
            if (widget.getParent() instanceof AbstractOrderedLayout) {
                ((HasWeight) widget).setWeight(weightSlider.getValue().intValue());
            }
            if (widget.getParent() instanceof GridLayout) {
                boolean isValid = LayoutUtils.validateSpan(
                        (GridLayout) widget.getParent(),
                        tree,
                        (HasGridSpan) widget,
                        colSpanSlider.getValue().intValue(),
                        rowSpanSlider.getValue().intValue()
                );

                if (isValid) {
                    int colSpan = colSpanSlider.getValue().intValue();
                    int rowSpan = rowSpanSlider.getValue().intValue();

                    ((HasGridSpan) widget).setColSpan(colSpan);
                    ((HasGridSpan) widget).setRowSpan(rowSpan);

                    GridCell gridCell = LayoutUtils.getWidgetCell(tree, widget);
                    TreeUtils.markGridCells(tree,
                            (GridLayout) widget.getParent(),
                            gridCell.getRow(),
                            gridCell.getColumn(),
                            rowSpan,
                            colSpan);
                } else {
                    showNotification(getMessage("notValidated"), NotificationType.ERROR);
                }
            }
            close("");
        });
        buttonsLayout.addComponent(okButton);

        Button cancelButton = new Button(getMessage("cancel"), FontAwesome.TIMES);
        cancelButton.addClickListener((Button.ClickListener) event ->
                close("")
        );
        buttonsLayout.addComponent(cancelButton);

        layout.addComponent(buttonsLayout);
    }
}