/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum WidgetParameterType implements EnumClass<Integer> {

    ENTITY(10),
    LIST_ENTITY(20),
    DATE(30),
    INTEGER(40),
    STRING(50),
    DECIMAL(60);

    private Integer id;

    WidgetParameterType(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static WidgetParameterType fromId(Integer id) {
        for (WidgetParameterType at : WidgetParameterType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}