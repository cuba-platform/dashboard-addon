/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.frames.editor;

import com.haulmont.addon.dashboard.web.dashboard.events.DashboardRefreshEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.WidgetSelectedEvent;

public interface DashboardLayoutHolderComponent {

    void onWidgetSelectedEvent(WidgetSelectedEvent event);

    void onLayoutRefreshedEvent(DashboardRefreshEvent event);
}
