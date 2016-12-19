/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum DemoContentType implements EnumClass<Integer> {

    PRODUCTS_TABLE(10),
    SALES_CHART(20),
    INVOICE_REPORT(30);

    private Integer id;

    DemoContentType(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static DemoContentType fromId(Integer id) {
        for (DemoContentType at : DemoContentType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}