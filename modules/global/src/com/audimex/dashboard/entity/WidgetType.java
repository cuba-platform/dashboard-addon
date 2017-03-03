/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum WidgetType implements EnumClass<String> {

    VERTICAL_LAYOUT("VERTICAL_LAYOUT"),
    HORIZONTAL_LAYOUT("HORIZONTAL_LAYOUT"),
    GRID_LAYOUT("GRID_LAYOUT"),
    FRAME_PANEL("FRAME_PANEL"),
    GRID_CELL("GRID_CELL"),
    GRID_ROW("GRID_ROW");

    private String id;

    WidgetType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static WidgetType fromId(String id) {
        for (WidgetType at : WidgetType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}