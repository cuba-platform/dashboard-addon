/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.vaadin_components.layouts;

import com.vaadin.ui.Component;
import fi.jasoft.dragdroplayouts.DDGridLayout;

public class CanvasGridLayout extends AbstractCanvasLayout {
    protected DDGridLayout gridLayout;

    public CanvasGridLayout(int cols, int rows) {
        super(new DDGridLayout());
        gridLayout = (DDGridLayout) delegate;

        gridLayout.setColumns(cols);
        gridLayout.setRows(rows);
    }

    @Override
    public DDGridLayout getDelegate() {
        return gridLayout;
    }

    public void addComponent(Component component, int column, int row) {
        gridLayout.addComponent(component, column, row);
    }
}
