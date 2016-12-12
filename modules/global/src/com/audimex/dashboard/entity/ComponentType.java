/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum ComponentType implements EnumClass<String> {

    VERTICAL_LAYOUT("VERTICAL_LAYOUT"),
    HORIZONTAL_LAYOUT("HORIZONTAL_LAYOUT"),
    WIDGET("WIDGET");

    private String id;

    ComponentType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static ComponentType fromId(String id) {
        for (ComponentType at : ComponentType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}