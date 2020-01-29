/*
 * Copyright (c) 2008-2019 Haulmont.
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
 */
package com.haulmont.addon.dashboard.web.dashboard.frames.view;

import com.haulmont.addon.dashboard.gui.components.DashboardFrame;
import com.haulmont.addon.dashboard.web.dashboard.frames.uicomponent.WebDashboardFrame;
import com.haulmont.cuba.gui.Fragments;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.screen.MapScreenOptions;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

import javax.inject.Inject;
import java.util.Map;

@UiController("dashboard$PersistentDashboard.view")
@UiDescriptor("dashboard-view.xml")
public class DashboardView extends AbstractWindow {

    public static final String CODE = "CODE";
    public static final String DISPLAY_NAME = "DISPLAY_NAME";

    @Inject
    protected Fragments fragments;

    protected DashboardFrame dashboardFrame;

    @Override
    public void init(Map<String, Object> params) {
        if (params.containsKey(DISPLAY_NAME)) {
            setCaption((String) params.get(DISPLAY_NAME));
        }
        WebDashboardFrame webDashboardFrame = (WebDashboardFrame) fragments.create(this, WebDashboardFrame.class, new MapScreenOptions(params)).init();
        add(webDashboardFrame.getFragment());
        dashboardFrame = webDashboardFrame;
    }

    public DashboardFrame getDashboardFrame() {
        return dashboardFrame;
    }


}