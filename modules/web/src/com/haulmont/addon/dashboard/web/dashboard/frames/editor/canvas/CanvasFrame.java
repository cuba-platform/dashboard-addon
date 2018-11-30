/*
 * Copyright (c) 2008-2018 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas;

import com.haulmont.addon.dashboard.gui.components.DashboardFrame;
import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.web.DashboardException;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasRootLayout;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasWidgetLayout;
import com.haulmont.addon.dashboard.web.dashboard.tools.DashboardModelConverter;
import com.haulmont.addon.dashboard.web.widget.RefreshableWidget;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.ComponentContainer;
import com.haulmont.cuba.gui.components.OrderedContainer;

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
    protected OrderedContainer canvas;
    @Named("uiModelConverter")
    protected DashboardModelConverter converter;
    @WindowParam
    protected DashboardFrame dashboardFrame;
    @WindowParam
    protected Dashboard dashboard;

    protected CanvasRootLayout vLayout;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        updateLayout(dashboard);
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
        vLayout = (CanvasRootLayout) getConverter().modelToContainer(this, dashboard.getVisualModel());
        canvas.removeAll();
        canvas.add(vLayout);
    }

    public List<RefreshableWidget> getRefreshableWidgets() {
        List<RefreshableWidget> result = new ArrayList<>();
        searchRefreshableWidgets(vLayout, result);
        return result;
    }

    protected void searchRefreshableWidgets(ComponentContainer layout, List<RefreshableWidget> wbList) {
        if (layout instanceof CanvasWidgetLayout) {
            Component wb = getWidgetFrame((CanvasWidgetLayout) layout);
            if (RefreshableWidget.class.isAssignableFrom(wb.getClass())) {
                wbList.add((RefreshableWidget) wb);
            }
        } else {
            for (Component child : layout.getOwnComponents()) {
                if (child instanceof ComponentContainer) {
                    searchRefreshableWidgets((ComponentContainer) child, wbList);
                }
            }
        }
    }

    private Component getWidgetFrame(CanvasWidgetLayout layout) {
        return layout.getWidgetComponent();
    }

    public CanvasRootLayout getvLayout() {
        return vLayout;
    }
}
