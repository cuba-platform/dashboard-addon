/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.layouts;

public interface HasGridSpan {
    void setColSpan(int colSpan);
    int getColSpan();

    void setRowSpan(int rowSpan);
    int getRowSpan();
}
