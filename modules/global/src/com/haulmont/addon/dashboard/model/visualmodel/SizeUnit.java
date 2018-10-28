/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.model.visualmodel;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

public enum SizeUnit implements EnumClass<String> {

    PERCENTAGE("%"),
    PIXELS("px");

    private String id;

    SizeUnit(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static SizeUnit fromId(String id) {
        for (SizeUnit at : SizeUnit.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
