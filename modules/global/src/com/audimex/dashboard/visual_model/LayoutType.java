/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.visual_model;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum LayoutType implements EnumClass<String> {

    VERTICAL_LAYOUT("VERTICAL_LAYOUT"),
    HORIZONTAL_LAYOUT("HORIZONTAL_LAYOUT"),
    GRID_LAYOUT("GRID_LAYOUT"),
    GRID_AREA("GRID_AREA"),
    FRAME_PANEL("FRAME_PANEL");

    private String id;

    LayoutType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static LayoutType fromId(String id) {
        for (LayoutType at : LayoutType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}