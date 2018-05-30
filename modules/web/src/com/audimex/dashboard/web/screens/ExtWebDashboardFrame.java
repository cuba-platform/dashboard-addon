/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.screens;

import com.audimex.dashboard.web.dashboard.ui_component.WebDashboardFrame;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.web.gui.components.WebLabel;

import java.util.Map;

public class ExtWebDashboardFrame extends WebDashboardFrame {

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        Label label = new WebLabel();
        label.setValue("LALALALALALLA");
        this.add(label);
    }
}
