/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.settings;

import com.vaadin.ui.Component;

public interface DashboardSettings {
    String NAME = "amxd_DashboardSettings";

    boolean isComponentDraggable(Component component);
}
