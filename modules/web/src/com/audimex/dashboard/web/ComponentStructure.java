/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web;

import com.vaadin.ui.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by petunin on 07.12.2016.
 */
public class ComponentStructure {
    protected Component ownComponent;
    protected Component parentComponent;
    protected List<ComponentStructure> childNodes = new ArrayList<>();

    public ComponentStructure(Component ownComponent, Component parentComponent) {
        this.ownComponent = ownComponent;
        this.parentComponent = parentComponent;
    }

    public void addChild(Component component) {
        childNodes.add(new ComponentStructure(component, ownComponent));
    }

    public List<ComponentStructure> getChildNodes() {
        return childNodes;
    }

    public Component getOwnComponent() {
        return ownComponent;
    }

    public Component getParentComponent() {
        return parentComponent;
    }
}
