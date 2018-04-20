/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.entity.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum ParameterType implements EnumClass<Integer> {

    ENTITY(10),
    LIST_ENTITY(20),
    DATE(30),
    INTEGER(40),
    STRING(50),
    DECIMAL(60),
    BOOLEAN(70),
    LONG(80),
    UNDEFINED(90);

    private Integer id;

    ParameterType(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static ParameterType fromId(Integer id) {
        for (ParameterType at : ParameterType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}