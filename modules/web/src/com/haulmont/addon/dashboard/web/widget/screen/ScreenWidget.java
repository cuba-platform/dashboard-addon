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

package com.haulmont.addon.dashboard.web.widget.screen;


import com.haulmont.addon.dashboard.gui.components.DashboardFrame;
import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.web.annotation.WidgetParam;
import com.haulmont.addon.dashboard.web.repository.WidgetRepository;
import com.haulmont.cuba.gui.Fragments;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Fragment;
import com.haulmont.cuba.gui.components.ScrollBoxLayout;
import com.haulmont.cuba.gui.screen.MapScreenOptions;
import com.haulmont.cuba.gui.screen.ScreenFragment;

import javax.inject.Inject;
import java.util.Map;

//@DashboardWidget(name = CAPTION, editFrameId = "dashboard$ScreenWidget.edit")
public class ScreenWidget extends AbstractFrame {

    public static final String CAPTION = "Screen";

    @WidgetParam
    @WindowParam
    protected String screenId;

    @Inject
    protected WidgetRepository widgetRepository;

    @WindowParam
    protected Widget widget;

    @WindowParam
    protected Dashboard dashboard;

    @WindowParam
    protected DashboardFrame dashboardFrame;

    @Inject
    private ScrollBoxLayout scroll;

    protected Fragment fragment;

    @Inject
    private Fragments fragments;

    @Override
    public void init(Map<String, Object> params) {
        ScreenFragment screenFragment = fragments.create(getFrameOwner(), screenId,
                new MapScreenOptions(widgetRepository.getWidgetParams(widget)));
        if (screenFragment != null) {
            fragment = screenFragment.getFragment();
            scroll.add(fragment);
        }
    }
}
