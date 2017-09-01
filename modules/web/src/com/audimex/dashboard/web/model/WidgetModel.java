/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.model;

import com.audimex.dashboard.entity.WidgetType;
import com.audimex.dashboard.entity.WidgetViewType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WidgetModel implements Serializable {
    protected String frameId;
    protected String caption;
    protected String icon;
    protected UUID templateWidgetId;
    protected String entityType;
    protected int weight = 1;
    protected int colspan = 1;
    protected int rowspan = 1;
    protected int column = 1;
    protected int row = 1;
    protected int gridColumnCount = 1;
    protected int gridRowCount = 1;
    protected List<WidgetModel> children = new ArrayList<>();
    protected WidgetType type;
    protected List<WidgetLinkModel> widgetLinks = new ArrayList<>();
    protected WidgetViewType viewType;


    public WidgetViewType getViewType() {
        return viewType;
    }

    public void setViewType(WidgetViewType viewType) {
        this.viewType = viewType;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

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

    public List<WidgetLinkModel> getWidgetLinks() {
        return widgetLinks;
    }

    public void setWidgetLinks(List<WidgetLinkModel> widgetLinks) {
        this.widgetLinks = widgetLinks;
    }

    public WidgetType getType() {
        return type;
    }

    public void setType(WidgetType type) {
        this.type = type;
    }

    public int getGridColumnCount() {
        return gridColumnCount;
    }

    public void setGridColumnCount(int gridColumnCount) {
        this.gridColumnCount = gridColumnCount;
    }

    public int getGridRowCount() {
        return gridRowCount;
    }

    public void setGridRowCount(int gridRowCount) {
        this.gridRowCount = gridRowCount;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public UUID getTemplateWidgetId() {
        return templateWidgetId;
    }

    public void setTemplateWidgetId(UUID templateWidgetId) {
        this.templateWidgetId = templateWidgetId;
    }
}
