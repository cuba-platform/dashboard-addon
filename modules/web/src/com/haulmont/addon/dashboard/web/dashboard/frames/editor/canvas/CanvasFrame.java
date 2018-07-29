/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas;

import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.visual_model.VerticalLayout;
import com.haulmont.addon.dashboard.web.DashboardException;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasLayout;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasVerticalLayout;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasWidgetLayout;
import com.haulmont.addon.dashboard.web.dashboard.tools.DashboardModelConverter;
import com.haulmont.addon.dashboard.gui.components.WidgetBrowse;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.addon.dashboard.web.DashboardException;
import com.haulmont.addon.dashboard.web.dashboard.tools.DashboardModelConverter;

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

        vLayout.removeStyleName("amxd-shadow-border");

        canvas.removeAll();
        canvas.add(vLayout);
    }

    public WidgetBrowse getWidgetBrowse(String widgetId) {
        return searchWidgetBrowse(vLayout, widgetId);
    }

    protected WidgetBrowse searchWidgetBrowse(CanvasLayout layout, String widgetId) {
        if (layout instanceof CanvasWidgetLayout) {
            if (widgetId.equals(((CanvasWidgetLayout) layout).getWidget().getWidgetId())) {
                return (WidgetBrowse) ((Container) layout.getDelegate()).getOwnComponents().iterator().next();
            }
            return null;
        }

        for (Component child : ((Container) layout.getDelegate()).getOwnComponents()) {
            WidgetBrowse result = searchWidgetBrowse((CanvasLayout) child, widgetId);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
