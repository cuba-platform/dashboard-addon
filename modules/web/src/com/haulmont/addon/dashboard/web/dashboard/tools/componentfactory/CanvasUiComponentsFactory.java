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

package com.haulmont.addon.dashboard.web.dashboard.tools.componentfactory;

import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.model.visualmodel.*;
import com.haulmont.addon.dashboard.web.DashboardException;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame;
import com.haulmont.addon.dashboard.web.dashboard.layouts.*;
import com.haulmont.addon.dashboard.web.repository.WidgetRepository;
import com.haulmont.addon.dashboard.web.repository.WidgetTypeInfo;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.VBoxLayout;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;

@Component("dashboard_uiComponentsFactory")
public class CanvasUiComponentsFactory implements CanvasComponentsFactory {

    public static final String WIDGET = "widget";
    public static final String DASHBOARD = "dashboard";
    public static final String DASHBOARD_FRAME = "dashboardFrame";

    @Inject
    protected WidgetRepository widgetRepository;

    @Inject
    protected UiComponents components;

    @Override
    public CanvasVerticalLayout createCanvasVerticalLayout(VerticalLayout verticalLayout) {
        CanvasVerticalLayout layout = new CanvasVerticalLayout(verticalLayout);
        layout.setUuid(UUID.randomUUID());
        layout.setSizeFull();
        return layout;
    }

    @Override
    public CanvasHorizontalLayout createCanvasHorizontalLayout(HorizontalLayout horizontalLayout) {
        CanvasHorizontalLayout layout = new CanvasHorizontalLayout(horizontalLayout);
        layout.setUuid(UUID.randomUUID());
        layout.setSizeFull();
        return layout;
    }

    @Override
    public CanvasCssLayout createCssLayout(CssLayout cssLayoutModel) {
        CanvasCssLayout layout = new CanvasCssLayout(cssLayoutModel);
        layout.setUuid(UUID.randomUUID());
        layout.setSizeFull();
        return layout;
    }

    @Override
    public CanvasGridLayout createCanvasGridLayout(GridLayout gridLayout) {
        CanvasGridLayout layout = new CanvasGridLayout(gridLayout);
        layout.setUuid(UUID.randomUUID());
        layout.setSizeFull();
        return layout;
    }

    @Override
    public CanvasWidgetLayout createCanvasWidgetLayout(CanvasFrame frame, WidgetLayout widgetLayout) {
        Widget widget = widgetLayout.getWidget();
        Optional<WidgetTypeInfo> widgetTypeOpt = widgetRepository.getWidgetTypesInfo().stream()
                .filter(widgetType -> widget.getFrameId().equals(widgetType.getFrameId()))
                .findFirst();

        if (!widgetTypeOpt.isPresent()) {
            throw new DashboardException(format("There isn't found a screen for the widget %s", widget.getFrameId()));
        }

        widget.setDashboard(frame.getDashboard());

        String frameId = widgetTypeOpt.get().getFrameId();
        Map<String, Object> params = new HashMap<>(ParamsMap.of(
                WIDGET, widget,
                DASHBOARD, frame.getDashboard(),
                DASHBOARD_FRAME, frame.getDashboardFrame()
        ));
        params.putAll(widgetRepository.getWidgetParams(widget));

        //TODO remove deprecated code
        AbstractFrame widgetFrame = frame.openFrame(null, frameId, params);

        widgetFrame.setSizeFull();

        com.haulmont.cuba.gui.components.Component widgetComponent = widgetFrame;

        if (BooleanUtils.isTrue(widget.getShowWidgetCaption())) {
            VBoxLayout vBoxLayout = components.create(VBoxLayout.class);
            vBoxLayout.setSpacing(true);
            vBoxLayout.setMargin(true);
            vBoxLayout.setSizeFull();

            Label<String> label = components.create(Label.class);
            label.setValue(widget.getCaption());
            label.setStyleName("h2");
            vBoxLayout.add(label);

            vBoxLayout.add(widgetFrame);
            vBoxLayout.expand(widgetFrame);
            widgetComponent = vBoxLayout;
        } else {
            widgetFrame.setMargin(true);
        }

        CanvasWidgetLayout layout = new CanvasWidgetLayout(widgetLayout);
        layout.setUuid(UUID.randomUUID());
        layout.addComponent(widgetComponent);
        layout.setWidgetComponent(widgetFrame);
        layout.setInnerLayout(widgetComponent);
        layout.setWidget(widget);
        layout.getDelegate().expand(widgetComponent);
        layout.setSizeFull();
        return layout;
    }

    @Override
    public CanvasRootLayout createCanvasRootLayout(RootLayout rootLayout) {
        CanvasRootLayout layout = new CanvasRootLayout(rootLayout);
        layout.setUuid(UUID.randomUUID());
//        layout.setSizeFull();
        return layout;
    }
}
