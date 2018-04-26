/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.model;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

public enum ParameterType implements EnumClass<String> {

    ENTITY("ENTITY"),
    LIST_ENTITY("LIST_ENTITY"),
    ENUM("ENUM"),
    DATE("DATE"),
    DATETIME("DATETIME"),
    TIME("TIME"),
    UUID("UUID"),
    INTEGER("INTEGER"),
    STRING("STRING"),
    DECIMAL("DECIMAL"),
    BOOLEAN("BOOLEAN"),
    LONG("LONG"),
    UNDEFINED("UNDEFINED");

    private String id;

    ParameterType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static ParameterType fromId(String id) {
        for (ParameterType at : ParameterType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}