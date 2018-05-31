/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.dashboard_group;

import com.audimex.dashboard.entity.DashboardGroup;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.actions.AddAction;

import javax.inject.Named;
import java.util.Map;

public class DashboardGroupEdit extends AbstractEditor<DashboardGroup> {
    @Named("dashboardsTable.add")
    protected AddAction add;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        add.setWindowId("dashboardBrowse");
    }
}