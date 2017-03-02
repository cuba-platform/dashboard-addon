/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum DropTarget implements EnumClass<Integer> {

    LAYOUT(1),
    TREE(2),
    REORDER(3);

    protected Integer id;

    DropTarget(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static DropTarget fromId(Integer id) {
        for (DropTarget at : DropTarget.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}