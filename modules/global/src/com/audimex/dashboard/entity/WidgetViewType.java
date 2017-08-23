/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum WidgetViewType implements EnumClass<Integer> {

    COMMON(10),
    LIST(20),
    CHART(30);

    private Integer id;

    WidgetViewType(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static WidgetViewType fromId(Integer id) {
        for (WidgetViewType at : WidgetViewType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}