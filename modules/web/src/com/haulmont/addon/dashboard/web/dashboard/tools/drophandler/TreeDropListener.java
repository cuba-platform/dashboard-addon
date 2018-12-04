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
 */

package com.haulmont.addon.dashboard.web.dashboard.tools.drophandler;

import com.haulmont.addon.dashboard.model.visualmodel.DashboardLayout;
import com.haulmont.addon.dashboard.web.dashboard.events.WidgetAddedEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.WidgetLayoutEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.WidgetMovedEvent;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Events;
import com.vaadin.shared.ui.grid.DropLocation;
import com.vaadin.ui.components.grid.TreeGridDropEvent;
import com.vaadin.ui.components.grid.TreeGridDropListener;

public class TreeDropListener implements TreeGridDropListener {

    private Events events = AppBeans.get(Events.class);

    @Override
    public void drop(TreeGridDropEvent e) {
        DashboardLayout target = (DashboardLayout) e.getDropTargetRow().get();
        DashboardLayout source = (DashboardLayout) e.getDragData().get();
        WidgetLayoutEvent.WidgetDropLocation dropLocation = getDropLocation(e.getDropLocation());
        if (source.getId() == null) {
            events.publish(new WidgetAddedEvent(source, target.getUuid(), dropLocation));
        } else {
            events.publish(new WidgetMovedEvent(source, target.getUuid(), dropLocation));
        }
    }

    private WidgetLayoutEvent.WidgetDropLocation getDropLocation(DropLocation dropLocation) {
        switch (dropLocation) {
            case ABOVE:
                return WidgetLayoutEvent.WidgetDropLocation.TOP;
            case ON_TOP:
                return WidgetLayoutEvent.WidgetDropLocation.MIDDLE;
            case BELOW:
                return WidgetLayoutEvent.WidgetDropLocation.BOTTOM;
        }
        return WidgetLayoutEvent.WidgetDropLocation.MIDDLE;
    }

}
