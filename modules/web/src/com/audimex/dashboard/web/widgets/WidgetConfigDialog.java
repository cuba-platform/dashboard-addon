/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.widgets;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import java.awt.*;

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
        captionTextField.setWidth("300px");
        captionTextField.setCaption("Header caption");
        captionTextField.setValue(widget.getHeaderText());
        captionTextField.setRequired(true);
        layout.addComponent(captionTextField);

        ComboBox captionIconField = new ComboBox();
        captionIconField.setWidth("300px");
        captionIconField.setCaption("Header icon");
        captionIconField.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
        fillCaptionIcons(captionIconField);
        captionIconField.setNullSelectionAllowed(false);
        captionIconField.setValue(widget.getHeaderIcon());
        layout.addComponent(captionIconField);

        ComboBox weightComboBox = new ComboBox();
        weightComboBox.setWidth("300px");
        weightComboBox.setCaption("Weight in layout");
        for (int i = 1; i <= 10; i++) {
            weightComboBox.addItem(i);
        }
        weightComboBox.setNullSelectionAllowed(false);

        if (widget.getParent() instanceof AbstractOrderedLayout) {
            layout.addComponent(weightComboBox);
        }

        HorizontalLayout spanLayout = new HorizontalLayout();
        spanLayout.setSpacing(true);

        ComboBox colSpanComboBox = new ComboBox();
        colSpanComboBox.setCaption("Column span");
        colSpanComboBox.setWidth("100%");
        for (int i = 1; i < 10; i++) {
            colSpanComboBox.addItem(i);
        }
        spanLayout.addComponent(colSpanComboBox);

        ComboBox rowSpanComboBox = new ComboBox();
        rowSpanComboBox.setCaption("Row span");
        rowSpanComboBox.setWidth("100%");
        for (int i = 1; i < 10; i++) {
            rowSpanComboBox.addItem(i);
        }
        spanLayout.addComponent(rowSpanComboBox);

        if (widget.getParent() instanceof GridLayout) {
            layout.addComponent(spanLayout);
        }

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setSpacing(true);
        Button okButton = new Button("Ok", FontAwesome.CHECK);
        okButton.addClickListener((Button.ClickListener) event -> {
            widget.setHeaderText(captionTextField.getValue());
            widget.setHeaderIcon(captionIconField.getItemIcon(captionIconField.getValue()));

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