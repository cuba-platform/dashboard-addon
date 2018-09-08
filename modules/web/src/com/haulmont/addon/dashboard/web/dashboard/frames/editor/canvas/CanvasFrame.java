/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas;

import com.haulmont.addon.dashboard.gui.components.DashboardFrame;
import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.visualmodel.VerticalLayout;
import com.haulmont.addon.dashboard.web.DashboardException;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasLayout;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasVerticalLayout;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasWidgetLayout;
import com.haulmont.addon.dashboard.web.dashboard.tools.DashboardModelConverter;
import com.haulmont.addon.dashboard.web.widget.RefreshableWidget;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.VBoxLayout;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CanvasFrame extends AbstractFrame {
    public static final String SCREEN_NAME = "dashboard$CanvasFrame";
    public static final String DASHBOARD = "dashboard";
    public static final String DASHBOARD_FRAME = "dashboardFrame";

    @Inject
    protected VBoxLayout canvas;
    @Named("uiModelConverter")
    protected DashboardModelConverter converter;
    @WindowParam
    protected DashboardFrame dashboardFrame;
    @WindowParam
    protected Dashboard dashboard;

    protected CanvasVerticalLayout vLayout;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        updateLayout(dashboard);
    }

    public VerticalLayout getDashboardModel() {
        return getConverter().containerToModel(vLayout);
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public DashboardFrame getDashboardFrame() {
        return dashboardFrame;
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

        vLayout.removeStyleName("dashboard-shadow-border");

        canvas.removeAll();
        canvas.add(vLayout);
    }

    public List<RefreshableWidget> getRefreshableWidgets() {
        List<RefreshableWidget> result = new ArrayList<>();
        searchRefreshableWidgets(vLayout, result);
        return result;
    }

    protected void searchRefreshableWidgets(CanvasLayout layout, List<RefreshableWidget> wbList) {
        if (layout instanceof CanvasWidgetLayout) {
            Component wb = ((Container) layout.getDelegate()).getOwnComponents().iterator().next();
            if (RefreshableWidget.class.isAssignableFrom(wb.getClass())) {
                wbList.add((RefreshableWidget) wb);
            }
        } else {
            for (Component child : ((Container) layout.getDelegate()).getOwnComponents()) {
                searchRefreshableWidgets((CanvasLayout) child, wbList);
            }
        }
    }

    public CanvasVerticalLayout getvLayout() {
        return vLayout;
    }
}
