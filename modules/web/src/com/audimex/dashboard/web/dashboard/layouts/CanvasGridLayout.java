/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.layouts;

import com.haulmont.addon.dnd.web.gui.components.WebDDGridLayout;
import com.haulmont.cuba.gui.components.Component;

public class CanvasGridLayout extends AbstractCanvasLayout {
    protected WebDDGridLayout gridLayout;

    public CanvasGridLayout(int cols, int rows) {
        super(new WebDDGridLayout());
        gridLayout = (WebDDGridLayout) delegate;

        gridLayout.setColumns(cols);
        gridLayout.setRows(rows);
    }

    @Override
    public WebDDGridLayout getDelegate() {
        return gridLayout;
    }

    public void addComponent(Component component, int column, int row) {
        gridLayout.add(component, column, row);
    }
}
