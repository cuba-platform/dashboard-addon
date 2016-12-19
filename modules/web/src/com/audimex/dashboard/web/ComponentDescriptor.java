/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web;

import com.audimex.dashboard.entity.ComponentType;
import com.vaadin.ui.Component;

public class ComponentDescriptor {
    protected Component ownComponent;

    protected ComponentType componentType;

    protected int column;
    protected int row;

    protected String caption;
    protected String icon;

    protected int weight;
    protected int colSpan;
    protected int rowSpan;

    public Component getOwnComponent() {
        return ownComponent;
    }

    public void setOwnComponent(Component ownComponent) {
        this.ownComponent = ownComponent;
    }

    public ComponentType getComponentType() {
        return componentType;
    }

    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
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

    public int getColSpan() {
        return colSpan;
    }

    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
    }

    public int getRowSpan() {
        return rowSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
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

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public ComponentDescriptor(Component ownComponent, ComponentType componentType) {
        this.ownComponent = ownComponent;
        this.componentType = componentType;
    }
}
