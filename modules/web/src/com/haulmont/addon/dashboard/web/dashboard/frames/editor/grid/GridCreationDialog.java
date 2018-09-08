/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.haulmont.addon.dashboard.web.dashboard.frames.editor.grid;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.HBoxLayout;
import com.vaadin.data.Property;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Slider;

import javax.inject.Inject;
import java.util.Map;

public class GridCreationDialog extends AbstractWindow {
    public static final String SCREEN_NAME = "dashboard$GridDialog";

    @Inject
    protected HBoxLayout slidersBox;

    protected Slider cols = new Slider(1, 16);
    protected Slider rows = new Slider(1, 16);

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        cols.setCaption(formatMessage("dashboard.gridColumnCount", 2));
        rows.setCaption(formatMessage("dashboard.gridRowCount", 2));
        cols.setValue((double) 2);
        rows.setValue((double) 2);
        cols.setWidth(100, Sizeable.Unit.PERCENTAGE);
        rows.setWidth(100, Sizeable.Unit.PERCENTAGE);
        cols.setCaptionAsHtml(true);
        rows.setCaptionAsHtml(true);

        cols.addValueChangeListener((Property.ValueChangeListener) event ->
                cols.setCaption(formatMessage("dashboard.gridColumnCount", cols.getValue().intValue()))
        );
        rows.addValueChangeListener((Property.ValueChangeListener) event ->
                rows.setCaption(formatMessage("dashboard.gridRowCount", rows.getValue().intValue()))
        );
        cols.focus();

        Layout hbox = slidersBox.unwrap(Layout.class);
        hbox.addComponent(cols);
        hbox.addComponent(rows);
    }

    public void apply() {
        this.close(COMMIT_ACTION_ID);
    }

    public void cancel() {
        this.close(CLOSE_ACTION_ID);
    }

    public int getRows() {
        return rows.getValue().intValue();
    }

    public int getCols() {
        return cols.getValue().intValue();
    }

}