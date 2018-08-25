/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas;

import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.model.visual_model.VerticalLayout;
import com.haulmont.addon.dashboard.web.DashboardException;
import com.haulmont.addon.dashboard.web.dashboard.layouts.AbstractCanvasLayout;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasLayout;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasVerticalLayout;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasWidgetLayout;
import com.haulmont.addon.dashboard.web.dashboard.tools.DashboardModelConverter;
import com.haulmont.addon.dashboard.gui.components.WidgetBrowse;
import com.haulmont.addon.dashboard.web.events.WidgetTreeElementClickedEvent;
import com.haulmont.addon.dashboard.web.widget_types.RefreshableWidget;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebAbstractComponent;
import org.springframework.context.event.EventListener;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public List<WidgetBrowse> getRefreshableWidgets() {
        List<WidgetBrowse> result = new ArrayList<>();
        searchRefreshableWidgets(vLayout, result);
        return result;
    }

    protected void searchRefreshableWidgets(CanvasLayout layout, List<WidgetBrowse> wbList) {
        if (layout instanceof CanvasWidgetLayout) {
            WidgetBrowse wb = (WidgetBrowse) ((Container) layout.getDelegate()).getOwnComponents().iterator().next();
            if (RefreshableWidget.class.isAssignableFrom(wb.getClass())) {
                wbList.add(wb);
            }
        } else {
            for (Component child : ((Container) layout.getDelegate()).getOwnComponents()) {
                searchRefreshableWidgets((CanvasLayout) child, wbList);
            }
        }
    }
}
