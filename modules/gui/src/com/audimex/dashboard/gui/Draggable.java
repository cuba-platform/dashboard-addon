/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.gui;

import com.audimex.dashboard.model.visual_model.DashboardLayout;
import com.haulmont.cuba.gui.components.Component;

public interface Draggable extends Component {
    DashboardLayout getLayout();

    void setLayout(DashboardLayout layout);
}
