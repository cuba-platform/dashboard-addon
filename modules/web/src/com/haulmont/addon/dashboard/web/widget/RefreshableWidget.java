package com.haulmont.addon.dashboard.web.widget;

import com.haulmont.addon.dashboard.web.events.DashboardEvent;

public interface RefreshableWidget  {
    void refresh(DashboardEvent dashboardEvent);
}
