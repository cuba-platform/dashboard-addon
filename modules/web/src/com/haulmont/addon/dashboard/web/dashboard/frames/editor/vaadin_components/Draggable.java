/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.frames.editor.vaadin_components;

import com.haulmont.addon.dashboard.model.visual_model.DashboardLayout;

public interface Draggable {
    DashboardLayout getLayout();

    void setLayout(DashboardLayout layout);
}
