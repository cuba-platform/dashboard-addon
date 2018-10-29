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

package com.haulmont.addon.dashboard.web.dashboard.tools.drophandler;

import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.web.dashboard.layouts.*;
import com.haulmont.cuba.gui.components.Component;

public class DropHandlerTools {

    protected Dashboard dashboard;

    public DropHandlerTools(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public void initDropHandler(Component component) {
        if (component instanceof CanvasLayout) {
            addDropHandler((CanvasLayout) component);
        }
        if (component instanceof Component.Container) {
            for (Component child : ((Component.Container) component).getOwnComponents()) {
                initDropHandler(child);
            }
        }
    }

    public void addDropHandler(CanvasLayout layout) {
        if (layout instanceof CanvasRootLayout) {
            layout.setDropHandler(new LayoutDropHandler(dashboard));
        } else if (layout instanceof CanvasVerticalLayout) {
            layout.setDropHandler(new LayoutDropHandler(dashboard));
        } else if (layout instanceof CanvasHorizontalLayout) {
            layout.setDropHandler(new LayoutDropHandler(dashboard));
        } else if (layout instanceof CanvasCssLayout) {
            layout.setDropHandler(new LayoutDropHandler(dashboard));
        }else if (layout instanceof CanvasGridLayout) {
            layout.setDropHandler(new NotDropHandler());
        } else if (layout instanceof CanvasWidgetLayout) {
            layout.setDropHandler(new NotDropHandler());
        }
    }

}
