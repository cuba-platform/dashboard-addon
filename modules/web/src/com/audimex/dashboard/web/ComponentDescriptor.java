/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web;

import com.audimex.dashboard.entity.ComponentType;
import com.vaadin.ui.Component;

/**
 * Created by petunin on 07.12.2016.
 */
public class ComponentDescriptor {
    protected Component ownComponent;
    protected ComponentType componentType;

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

    public ComponentDescriptor(Component ownComponent, ComponentType componentType) {
        this.ownComponent = ownComponent;
        this.componentType = componentType;
    }
}
