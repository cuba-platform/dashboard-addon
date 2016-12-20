/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.widgets;

import com.audimex.dashboard.entity.DemoContentType;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;

public class WidgetConfigDialog extends Window {

    public WidgetConfigDialog(WidgetPanel widget) {
        setCaption("Widget configuration");
        setIcon(FontAwesome.COGS);
        setResizable(false);
        setModal(true);

        setSizeUndefined();

        center();

        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setWidthUndefined();

        TextField captionTextField = new TextField();
        captionTextField.setImmediate(true);
        captionTextField.setNullRepresentation("");
        captionTextField.setWidth("300px");
        captionTextField.setCaption("Header caption");
        captionTextField.setValue(widget.getHeaderText());
        captionTextField.setRequired(true);
        layout.addComponent(captionTextField);

        ComboBox captionIconField = new ComboBox();
        captionIconField.setImmediate(true);
        captionIconField.setWidth("300px");
        captionIconField.setCaption("Header icon");
        captionIconField.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
        fillCaptionIcons(captionIconField);
        captionIconField.setNullSelectionAllowed(false);
        captionIconField.setValue(widget.getHeaderIcon());
        layout.addComponent(captionIconField);

        ComboBox contentTypeComboBox = new ComboBox();
        contentTypeComboBox.setImmediate(true);
        contentTypeComboBox.setCaption("Content");
        contentTypeComboBox.setWidth("300px");
        fillDemoContentTypes(contentTypeComboBox);
        contentTypeComboBox.setValue(widget.getDemoContentType());
        layout.addComponent(contentTypeComboBox);

        Slider weightSlider = new Slider();
        weightSlider.setImmediate(true);
        weightSlider.setWidth("300px");
        weightSlider.setCaption("Weight in layout");
        weightSlider.setMin(1);
        weightSlider.setMax(10);
        weightSlider.setResolution(0);
        weightSlider.setValue((double)widget.getWeight());

        if (widget.getParent() instanceof AbstractOrderedLayout) {
            layout.addComponent(weightSlider);
        }

        HorizontalLayout spanLayout = new HorizontalLayout();
        spanLayout.setWidth("300px");
        spanLayout.setSpacing(true);

        Slider colSpanSlider = new Slider();
        colSpanSlider.setImmediate(true);
        colSpanSlider.setCaption("Column span");
        colSpanSlider.setWidth("100%");
        colSpanSlider.setMin(1);
        colSpanSlider.setMax(10);
        colSpanSlider.setResolution(0);
        colSpanSlider.setValue((double)widget.getColSpan());
        spanLayout.addComponent(colSpanSlider);

        Slider rowSpanSlider = new Slider();
        rowSpanSlider.setImmediate(true);
        rowSpanSlider.setCaption("Row span");
        rowSpanSlider.setWidth("100%");
        rowSpanSlider.setMin(1);
        rowSpanSlider.setMax(10);
        rowSpanSlider.setResolution(0);
        rowSpanSlider.setValue((double)widget.getRowSpan());
        spanLayout.addComponent(rowSpanSlider);

        if (widget.getParent() instanceof GridLayout) {
            layout.addComponent(spanLayout);
        }

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setSpacing(true);
        Button okButton = new Button("Ok", FontAwesome.CHECK);
        okButton.addClickListener((Button.ClickListener) event -> {
            widget.setHeaderText(captionTextField.getValue());
            widget.setHeaderIcon(captionIconField.getItemIcon(captionIconField.getValue()));

            if (widget.getParent() instanceof AbstractOrderedLayout) {
                widget.setWeight(weightSlider.getValue().intValue());
            }
            if (widget.getParent() instanceof GridLayout) {
                widget.setColSpan(colSpanSlider.getValue().intValue());
                widget.setRowSpan(rowSpanSlider.getValue().intValue());
            }
            widget.setDemoContentType(((DemoContentType) contentTypeComboBox.getValue()));

            close();
        });
        buttonsLayout.addComponent(okButton);

        Button cancelButton = new Button("Cancel", FontAwesome.TIMES);
        cancelButton.addClickListener((Button.ClickListener) event ->
                close()
        );
        buttonsLayout.addComponent(cancelButton);

        layout.addComponent(buttonsLayout);

        setContent(layout);

        captionTextField.focus();
    }

    private void fillDemoContentTypes(ComboBox contentTypeComboBox) {
        contentTypeComboBox.addItem(DemoContentType.PRODUCTS_TABLE);
        contentTypeComboBox.setItemCaption(DemoContentType.PRODUCTS_TABLE, "Products table");
        contentTypeComboBox.setItemIcon(DemoContentType.PRODUCTS_TABLE, FontAwesome.TABLE);

        contentTypeComboBox.addItem(DemoContentType.SALES_CHART);
        contentTypeComboBox.setItemCaption(DemoContentType.SALES_CHART, "Sales chart");
        contentTypeComboBox.setItemIcon(DemoContentType.SALES_CHART, FontAwesome.LINE_CHART);

        contentTypeComboBox.addItem(DemoContentType.INVOICE_REPORT);
        contentTypeComboBox.setItemCaption(DemoContentType.INVOICE_REPORT, "Invoice report");
        contentTypeComboBox.setItemIcon(DemoContentType.INVOICE_REPORT, FontAwesome.FILE_O);
    }

    private void fillCaptionIcons(ComboBox captionIconField) {
        captionIconField.addItem(FontAwesome.ROCKET);
        captionIconField.setItemIcon(FontAwesome.ROCKET, FontAwesome.ROCKET);
        captionIconField.setItemCaption(FontAwesome.ROCKET, "ROCKET");

        captionIconField.addItem(FontAwesome.MAGIC);
        captionIconField.setItemIcon(FontAwesome.MAGIC, FontAwesome.MAGIC);
        captionIconField.setItemCaption(FontAwesome.MAGIC, "MAGIC");

        captionIconField.addItem(FontAwesome.DOLLAR);
        captionIconField.setItemIcon(FontAwesome.DOLLAR, FontAwesome.DOLLAR);
        captionIconField.setItemCaption(FontAwesome.DOLLAR, "DOLLAR");

        captionIconField.addItem(FontAwesome.BUILDING);
        captionIconField.setItemIcon(FontAwesome.BUILDING, FontAwesome.BUILDING);
        captionIconField.setItemCaption(FontAwesome.BUILDING, "BUILDING");

        captionIconField.addItem(FontAwesome.BOOKMARK_O);
        captionIconField.setItemIcon(FontAwesome.BOOKMARK_O, FontAwesome.BOOKMARK_O);
        captionIconField.setItemCaption(FontAwesome.BOOKMARK_O, "BUILDING");
    }
}