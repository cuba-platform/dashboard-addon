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

package com.haulmont.addon.dashboard.web.dashboard.layouts;

import com.haulmont.addon.dashboard.model.visualmodel.CssLayout;
import com.haulmont.addon.dashboard.web.dashboard.frames.uicomponent.WebDashboardFrame;
import com.haulmont.bali.events.Subscription;
import com.haulmont.cuba.gui.components.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class CanvasCssLayout extends AbstractCanvasLayout {

    public static final String NAME = "canvasCssLayout";

    private static Logger log = LoggerFactory.getLogger(WebDashboardFrame.class);

    protected com.haulmont.cuba.gui.components.CssLayout cssLayout;

    public CanvasCssLayout init(CssLayout cssLayoutModel) {
        init(cssLayoutModel, com.haulmont.cuba.gui.components.CssLayout.class);
        cssLayout = (com.haulmont.cuba.gui.components.CssLayout) delegate;

        cssLayout.setStyleName(cssLayoutModel.getStyleName());
        cssLayout.setResponsive(cssLayoutModel.getResponsive());
        return this;
    }

    @Override
    public com.haulmont.cuba.gui.components.CssLayout getDelegate() {
        return cssLayout;
    }

    public void addComponent(Component component) {
        cssLayout.add(component);
    }

    @Override
    public Subscription addLayoutClickListener(Consumer<LayoutClickEvent> listener) {
        log.info("Click listener is not supported yet in Css layout");
        return () -> {
        };
    }

    @Override
    public void removeLayoutClickListener(Consumer<LayoutClickEvent> listener) {
        log.info("Click listener is not supported yet in Css layout");
    }

}
