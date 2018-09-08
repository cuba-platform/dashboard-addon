/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.frames.editor.components;

import com.haulmont.addon.dashboard.model.visualmodel.DashboardLayout;

public interface Draggable {
    DashboardLayout getLayout();

    void setLayout(DashboardLayout layout);
}
