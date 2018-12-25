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

package com.haulmont.addon.dashboard.web.dashboard.tools.componentfactory;

import com.haulmont.addon.dashboard.model.visualmodel.DashboardLayout;
import com.haulmont.addon.dashboard.model.visualmodel.WidgetLayout;
import com.haulmont.addon.dashboard.web.DashboardIcon;
import com.haulmont.addon.dashboard.web.dashboard.events.CreateWidgetTemplateEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.model.*;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.gui.components.AbstractAction;
import com.haulmont.cuba.gui.components.Action;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.haulmont.addon.dashboard.utils.DashboardLayoutUtils.*;
import static com.haulmont.cuba.gui.icons.CubaIcon.*;

@Component(ActionProviderFactory.NAME)
public class ActionProviderFactoryImpl implements ActionProviderFactory {

    @Inject
    protected Events events;

    @Override
    public List<Action> getLayoutActions(DashboardLayout layout) {
        List<Action> actions = new ArrayList<>();
        if (layout != null) {
            if (!isGridCellLayout(layout) && !isRootLayout(layout)) {
                actions.add(createRemoveAction(layout));
            }
            if (layout instanceof WidgetLayout) {
                WidgetLayout widgetLayout = (WidgetLayout) layout;
                actions.add(createEditAction(widgetLayout));
                actions.add(createTemplateAction(widgetLayout));
            }
            if (!isRootLayout(layout) && !isGridCellLayout(layout) && !isParentCssLayout(layout) && !isParentHasExpand(layout)) {
                actions.add(createWeightAction(layout));
            }
            if (isGridCellLayout(layout)){
                actions.add(createColspanAction(layout));
            }
            if (isLinearLayout(layout)) {
                actions.add(createExpandAction(layout));
            }
            actions.add(createStyleAction(layout));
        }
        return actions;
    }

    private AbstractAction createStyleAction(DashboardLayout layout) {
        return new AbstractAction("style") {
            @Override
            public void actionPerform(com.haulmont.cuba.gui.components.Component component) {
                events.publish(new StyleChangedEvent(layout));
            }

            @Override
            public String getIcon() {
                return PAINT_BRUSH.source();
            }
        };
    }

    private AbstractAction createExpandAction(DashboardLayout layout) {
        return new AbstractAction("expand") {
            @Override
            public void actionPerform(com.haulmont.cuba.gui.components.Component component) {
                events.publish(new ExpandChangedEvent(layout));
            }

            @Override
            public String getIcon() {
                return EXPAND.source();
            }
        };
    }

    private AbstractAction createColspanAction(DashboardLayout layout) {
        return new AbstractAction("weight") {
            @Override
            public void actionPerform(com.haulmont.cuba.gui.components.Component component) {
                events.publish(new ColspanChangedEvent(layout));
            }

            @Override
            public String getIcon() {
                return ARROWS.source();
            }
        };
    }

    private AbstractAction createWeightAction(DashboardLayout layout) {
        return new AbstractAction("weight") {
            @Override
            public void actionPerform(com.haulmont.cuba.gui.components.Component component) {
                events.publish(new WeightChangedEvent(layout));
            }

            @Override
            public String getIcon() {
                return ARROWS.source();
            }
        };
    }

    private AbstractAction createTemplateAction(WidgetLayout widgetLayout) {
        return new AbstractAction("template") {
            @Override
            public void actionPerform(com.haulmont.cuba.gui.components.Component component) {
                events.publish(new CreateWidgetTemplateEvent(widgetLayout.getWidget()));
            }

            @Override
            public String getIcon() {
                return DATABASE.source();
            }
        };
    }

    private AbstractAction createEditAction(WidgetLayout widgetLayout) {
        return new AbstractAction("edit") {
            @Override
            public void actionPerform(com.haulmont.cuba.gui.components.Component component) {
                events.publish(new WidgetEditEvent(widgetLayout));
            }

            @Override
            public String getIcon() {
                return DashboardIcon.GEAR_ICON.source();
            }
        };
    }

    private AbstractAction createRemoveAction(DashboardLayout layout) {
        return new AbstractAction("remove") {
            @Override
            public void actionPerform(com.haulmont.cuba.gui.components.Component component) {
                events.publish(new WidgetRemovedEvent(layout));
            }

            @Override
            public String getIcon() {
                return DashboardIcon.TRASH_ICON.source();
            }
        };
    }
}
