/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum ParameterInputType implements EnumClass<Integer> {

    OUTER(10),
    ALGORITHM(20),
    INPUT(30);

    private Integer id;

    ParameterInputType(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static ParameterInputType fromId(Integer id) {
        for (ParameterInputType at : ParameterInputType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}