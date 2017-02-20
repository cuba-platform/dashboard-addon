/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.widgets;

import com.vaadin.ui.GridLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by petunin on 16.02.2017.
 */
public class GridRow implements Serializable {
    List<GridCell> gridCells = new ArrayList<>();
    GridLayout gridLayout;

    public GridRow(GridLayout gridLayout) {
        this.gridLayout = gridLayout;
    }

    public GridLayout getGridLayout() {
        return gridLayout;
    }

    public void setGridLayout(GridLayout gridLayout) {
        this.gridLayout = gridLayout;
    }

    public List<GridCell> getGridCells() {
        return gridCells;
    }

    public void setGridCells(List<GridCell> gridCells) {
        this.gridCells = gridCells;
    }

    public void addGridCell(GridCell gridCell) {
        gridCells.add(gridCell);
    }
}
