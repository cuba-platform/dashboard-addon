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

package com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas;

import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.visualmodel.RootLayout;
import com.haulmont.addon.dashboard.web.DashboardStyleConstants;
import com.haulmont.addon.dashboard.web.dashboard.events.CanvasLayoutElementClickedEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.DashboardRefreshEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.WidgetSelectedEvent;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.DashboardLayoutHolderComponent;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasLayout;
import com.haulmont.addon.dashboard.web.dashboard.tools.DashboardModelConverter;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.ComponentContainer;
import com.haulmont.cuba.gui.components.MouseEventDetails;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;
import org.springframework.context.event.EventListener;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

@UiController("dashboard$CanvasEditorFrame")
@UiDescriptor("canvas-editor-frame.xml")
public class CanvasEditorFrame extends CanvasFrame implements DashboardLayoutHolderComponent {

    private MouseEventDetails mouseEventDetails;

    @Named("dropModelConverter")
    protected DashboardModelConverter converter;

    @Inject
    protected Events events;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    public void updateLayout(Dashboard dashboard) {
        super.updateLayout(dashboard);
        vLayout.addStyleName("dashboard-main-shadow-border");
    }

    @Override
    protected DashboardModelConverter getConverter() {
        return converter;
    }

    protected void selectLayout(ComponentContainer layout, UUID layoutUuid, boolean needSelect) {
        if (layout instanceof CanvasLayout) {
            if (((CanvasLayout) layout).getUuid().equals(layoutUuid) && needSelect) {
                layout.addStyleName(DashboardStyleConstants.DASHBOARD_TREE_SELECTED);
            } else {
                layout.removeStyleName(DashboardStyleConstants.DASHBOARD_TREE_SELECTED);
            }
        }

        for (Component child : layout.getComponents()) {
            if (child instanceof ComponentContainer) {
                selectLayout((ComponentContainer) child, layoutUuid, needSelect);
            }
        }
    }

    @EventListener
    public void onWidgetSelectedEvent(WidgetSelectedEvent event) {
        UUID layoutUuid = event.getSource();
        selectLayout(vLayout, layoutUuid, true);
    }

    @EventListener
    public void onLayoutRefreshedEvent(DashboardRefreshEvent event) {
        RootLayout dashboardModel = (RootLayout) event.getSource();
        dashboard.setVisualModel(dashboardModel);
        updateLayout(dashboard);
    }

    @EventListener
    public void canvasLayoutElementClickedEventListener(CanvasLayoutElementClickedEvent event) {
        UUID layoutUuid = event.getSource();
        MouseEventDetails md = event.getMouseEventDetails();
        //TODO: refactor, problem with addLayoutClickListener in every layout prod a lot of events, in root can't get clicked comp
        if (mouseEventDetails == null) {
            mouseEventDetails = md;
        } else {
            if (mouseEventDetails.getClientX() == md.getClientX() && mouseEventDetails.getClientY() == md.getClientY()) {
                return;
            } else {
                mouseEventDetails = md;
            }
        }

        events.publish(new WidgetSelectedEvent(layoutUuid, WidgetSelectedEvent.Target.CANVAS));
    }
}
