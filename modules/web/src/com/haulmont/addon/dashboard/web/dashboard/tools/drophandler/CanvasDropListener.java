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

package com.haulmont.addon.dashboard.web.dashboard.tools.drophandler;

import com.haulmont.addon.dashboard.model.visualmodel.DashboardLayout;
import com.haulmont.addon.dashboard.web.dashboard.events.WidgetAddedEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.WidgetDropLocation;
import com.haulmont.addon.dashboard.web.dashboard.events.WidgetMovedEvent;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.web.widgets.CubaCssActionsLayout;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.dnd.event.DropEvent;
import com.vaadin.ui.dnd.event.DropListener;

import java.util.UUID;

public class CanvasDropListener implements DropListener<CubaCssActionsLayout> {

    private Events events = AppBeans.get(Events.class);

    @Override
    public void drop(DropEvent e) {
        if (e.getDragData().isPresent()) {
            DashboardLayout source = (DashboardLayout) e.getDragData().get();
            AbstractComponent targetComponent = e.getComponent();
            UUID targetLayoutId = targetComponent.getId() != null ? UUID.fromString(targetComponent.getId()) : null;
            if (targetLayoutId != null) {
                if (source.getId() == null) {
                    events.publish(new WidgetAddedEvent(source, targetLayoutId, WidgetDropLocation.MIDDLE));
                } else {
                    events.publish(new WidgetMovedEvent(source, targetLayoutId, (WidgetDropLocation) null));
                }
            }
        }
    }
}
