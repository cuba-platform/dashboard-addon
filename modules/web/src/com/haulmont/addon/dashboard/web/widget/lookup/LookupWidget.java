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

package com.haulmont.addon.dashboard.web.widget.lookup;

import com.haulmont.addon.dashboard.gui.components.DashboardFrame;
import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.web.annotation.DashboardWidget;
import com.haulmont.addon.dashboard.web.annotation.WidgetParam;
import com.haulmont.addon.dashboard.web.events.DashboardEvent;
import com.haulmont.addon.dashboard.web.events.ItemsSelectedEvent;
import com.haulmont.addon.dashboard.web.repository.WidgetRepository;
import com.haulmont.addon.dashboard.web.widget.RefreshableWidget;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.gui.Fragments;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.components.ScrollBoxLayout;
import com.haulmont.cuba.gui.screen.MapScreenOptions;
import com.haulmont.cuba.gui.screen.ScreenFragment;

import javax.inject.Inject;
import java.util.Map;

import static com.haulmont.addon.dashboard.web.widget.lookup.LookupWidget.CAPTION;


@DashboardWidget(name = CAPTION, editFrameId = "dashboard$LookupWidget.edit")
public class LookupWidget extends AbstractFrame implements RefreshableWidget {

    public static final String CAPTION = "Lookup";

    @Inject
    protected Events events;

    @Inject
    protected WidgetRepository widgetRepository;

    @WindowParam
    protected Widget widget;

    @WindowParam
    protected Dashboard dashboard;

    @WindowParam
    protected DashboardFrame dashboardFrame;

    protected LookupFragment lookupFragment;

    @WidgetParam
    @WindowParam
    protected String lookupWindowId;

    @Inject
    private ScrollBoxLayout scroll;

    @Inject
    private Fragments fragments;

    @Override
    public void init(Map<String, Object> params) {
        ScreenFragment screenFragment = fragments.create(getFrameOwner(), lookupWindowId,
                new MapScreenOptions(widgetRepository.getWidgetParams(widget)));
        if (screenFragment != null) {
            lookupFragment = (LookupFragment) screenFragment.getFragment();
            ListComponent lookupComponent = lookupFragment.getLookupComponent();
            lookupComponent.getDatasource().addItemChangeListener(e ->
                    events.publish(new ItemsSelectedEvent(widget, lookupComponent.getSelected())));
            scroll.add(lookupFragment);
        }

    }

    @Override
    public void refresh(DashboardEvent dashboardEvent) {
        lookupFragment.getLookupComponent().getDatasource().refresh();
    }

}
