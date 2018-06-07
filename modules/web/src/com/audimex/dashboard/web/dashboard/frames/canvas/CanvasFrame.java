/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.frames.canvas;

import com.audimex.dashboard.model.Dashboard;
import com.audimex.dashboard.model.visual_model.VerticalLayout;
import com.audimex.dashboard.web.DashboardException;
import com.audimex.dashboard.web.dashboard.layouts.CanvasVerticalLayout;
import com.audimex.dashboard.web.dashboard.tools.DashboardModelConverter;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.vaadin.ui.Layout;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class CanvasFrame extends AbstractFrame {
    public static final String SCREEN_NAME = "canvasFrame";
    public static final String DASHBOARD = "DASHBOARD";

    @Inject
    protected VBoxLayout canvas;
    @Named("uiModelConverter")
    protected DashboardModelConverter converter;

    protected CanvasVerticalLayout vLayout;
    protected Dashboard dashboard;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        dashboard = (Dashboard) params.get(DASHBOARD);
        updateLayout(dashboard);
    }

    public VerticalLayout getDashboardModel() {
        return getConverter().containerToModel(vLayout);
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    protected DashboardModelConverter getConverter() {
        return converter;
    }

    public void updateLayout(Dashboard dashboard) {
        if (dashboard == null) {
            throw new DashboardException("DASHBOARD parameter can not be null");
        }

        if (dashboard.getVisualModel() != null) {
            vLayout = (CanvasVerticalLayout) getConverter().modelToContainer(this, dashboard.getVisualModel());
        } else {
            vLayout = getConverter().getFactory().createCanvasVerticalLayout();
        }

        canvas.removeAll();
        canvas.unwrap(Layout.class).addComponent(vLayout);
    }
}
