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

package com.haulmont.addon.dashboard.web.dashboard.layouts;

import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.model.visualmodel.WidgetLayout;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.VBoxLayout;

public class CanvasWidgetLayout extends AbstractCanvasLayout {
    protected VBoxLayout verticalLayout;
    protected Widget widget = null;
    protected Component widgetComponent;
    protected Component innerLayout;

    public CanvasWidgetLayout(WidgetLayout model) {
        super(model, VBoxLayout.class);
        verticalLayout = (VBoxLayout) delegate;
    }

    @Override
    public VBoxLayout getDelegate() {
        return verticalLayout;
    }

    public Widget getWidget() {
        return widget;
    }

    public void setWidget(Widget widget) {
        this.widget = widget;
    }

    public Component getWidgetComponent() {
        return widgetComponent;
    }

    public void setWidgetComponent(Component widgetComponent) {
        this.widgetComponent = widgetComponent;
    }

    public Component getInnerLayout() {
        return innerLayout;
    }

    public void setInnerLayout(Component innerLayout) {
        this.innerLayout = innerLayout;
    }

}
