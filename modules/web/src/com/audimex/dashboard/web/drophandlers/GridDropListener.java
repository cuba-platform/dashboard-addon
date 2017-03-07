/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.drophandlers;

import com.audimex.dashboard.web.layouts.DashboardGridLayout;

public interface GridDropListener {
    void gridDropped(DashboardGridLayout gridLayout, Object targetLayout, int idx);
}
