/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.vaadin_components;

import com.audimex.dashboard.model.visual_model.DashboardLayout;

public interface Draggable {
    DashboardLayout getLayout();

    void setLayout(DashboardLayout layout);
}
