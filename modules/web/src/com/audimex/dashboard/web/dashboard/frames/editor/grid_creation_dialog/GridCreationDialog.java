/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.dashboard.frames.editor.grid_creation_dialog;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.HBoxLayout;
import com.vaadin.data.Property;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Slider;

import javax.inject.Inject;
import java.util.Map;

public class GridCreationDialog extends AbstractWindow {
    public static final String SCREEN_NAME = "grid-dialog";
    public static final String APPLY = "apply";
    public static final String CANCEL = "cancel";

    @Inject
    protected HBoxLayout slidersBox;

    protected Slider cols = new Slider(1, 16);
    protected Slider rows = new Slider(1, 16);

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        cols.setCaption(String.format(getMessage("dashboard.gridColumnCount"), 1));
        rows.setCaption(String.format(getMessage("dashboard.gridRowCount"), 1));
        cols.setValue((double) 2);
        rows.setValue((double) 2);
        cols.setWidth(100, Sizeable.Unit.PERCENTAGE);
        rows.setWidth(100, Sizeable.Unit.PERCENTAGE);
        cols.setCaptionAsHtml(true);
        rows.setCaptionAsHtml(true);

        cols.addValueChangeListener((Property.ValueChangeListener) event ->
                cols.setCaption(String.format(getMessage("dashboard.gridColumnCount"), cols.getValue().intValue()))
        );
        rows.addValueChangeListener((Property.ValueChangeListener) event ->
                rows.setCaption(String.format(getMessage("dashboard.gridRowCount"), rows.getValue().intValue()))
        );
        cols.focus();

        Layout hbox = slidersBox.unwrap(Layout.class);
        hbox.addComponent(cols);
        hbox.addComponent(rows);

    }

    public void apply() {
        this.close(APPLY);
    }

    public void cancel() {
        this.close(CANCEL);
    }

    public int getRows() {
        return rows.getValue().intValue();
    }

    public int getCols() {
        return cols.getValue().intValue();
    }

}