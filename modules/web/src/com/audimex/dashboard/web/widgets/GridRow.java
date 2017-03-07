/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.widgets;

import com.audimex.dashboard.web.layouts.DashboardGridLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GridRow implements Serializable {
    private List<GridCell> gridCells = new ArrayList<>();
    private DashboardGridLayout gridLayout;

    public GridRow(DashboardGridLayout gridLayout) {
        this.gridLayout = gridLayout;
    }

    public DashboardGridLayout getGridLayout() {
        return gridLayout;
    }

    public void setGridLayout(DashboardGridLayout gridLayout) {
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
