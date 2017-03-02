/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.model;

import com.audimex.dashboard.entity.WidgetType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WidgetModel implements Serializable {
    private String frameId;
    private int weight = 1;
    private int colspan = 1;
    private int rowspan = 1;
    private int column = 1;
    private int row = 1;
    private int columns = 2;
    private int rows = 2;
    private List<WidgetModel> children = new ArrayList<>();
    private WidgetType type;

    public String getFrameId() {
        return frameId;
    }

    public void setFrameId(String frameId) {
        this.frameId = frameId;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getColspan() {
        return colspan;
    }

    public void setColspan(int colspan) {
        this.colspan = colspan;
    }

    public int getRowspan() {
        return rowspan;
    }

    public void setRowspan(int rowspan) {
        this.rowspan = rowspan;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public List<WidgetModel> getChildren() {
        return children;
    }

    public void setChildren(List<WidgetModel> children) {
        this.children = children;
    }

    public WidgetType getType() {
        return type;
    }

    public void setType(WidgetType type) {
        this.type = type;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }
}
