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

package com.haulmont.addon.dashboard.web.dashboard.frames.browse;

import com.haulmont.addon.dashboard.entity.PersistentDashboard;
import com.haulmont.addon.dashboard.web.dashboard.frames.view.DashboardView;
import com.haulmont.addon.dashboard.web.dashboardgroup.DashboardGroupBrowse;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.screen.MapScreenOptions;
import com.haulmont.cuba.gui.screen.OpenMode;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

import javax.inject.Inject;
import java.util.UUID;

@UiController("dashboard$PersistentDashboard.browse")
@UiDescriptor("persistent-dashboard-browse.xml")
public class PersistentDashboardBrowse extends AbstractLookup {

    @Inject
    protected Screens screens;

    public void viewDashboard() {
        PersistentDashboard item = persistentDashboardsDs.getItem();
        if (item != null) {
            screens.create(DashboardView.class, OpenMode.NEW_TAB, new MapScreenOptions(ParamsMap.of(
                    DashboardView.CODE, item.getCode(),
                    DashboardView.DISPLAY_NAME, item.getName()))
            ).show();
        }

    }

    @Inject
    protected GroupDatasource<PersistentDashboard, UUID> persistentDashboardsDs;

    public void onDashboardGroupsBrowseClick() {
        screens.create(DashboardGroupBrowse.class, OpenMode.NEW_TAB).show();
    }
}
