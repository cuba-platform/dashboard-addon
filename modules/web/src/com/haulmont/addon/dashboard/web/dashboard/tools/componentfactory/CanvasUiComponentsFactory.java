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
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame;
import com.haulmont.addon.dashboard.web.dashboard.layouts.*;
import com.haulmont.addon.dashboard.web.repository.WidgetRepository;
import com.haulmont.addon.dashboard.web.repository.WidgetTypeInfo;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.gui.screen.MapScreenOptions;
import com.haulmont.cuba.web.AppUI;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component("dashboard_uiComponentsFactory")
public class CanvasUiComponentsFactory implements CanvasComponentsFactory {

    public static final String WIDGET = "widget";
    public static final String DASHBOARD = "dashboard";
    public static final String DASHBOARD_FRAME = "dashboardFrame";

    private static Logger log = LoggerFactory.getLogger(CanvasUiComponentsFactory.class);

    @Inject
    protected WidgetRepository widgetRepository;

    @Inject
    protected UiComponents components;

    @Inject
    protected Messages messages;

    @Inject
    protected AppUI appUI;

    @Override
    public CanvasVerticalLayout createCanvasVerticalLayout(VerticalLayout verticalLayout) {
        CanvasVerticalLayout layout = components.create(CanvasVerticalLayout.class).init(verticalLayout);
        layout.setUuid(UUID.randomUUID());
        layout.setSizeFull();
        return layout;
    }

    @Override
    public CanvasHorizontalLayout createCanvasHorizontalLayout(HorizontalLayout horizontalLayout) {
        CanvasHorizontalLayout layout = components.create(CanvasHorizontalLayout.class).init(horizontalLayout);
        layout.setUuid(UUID.randomUUID());
        layout.setSizeFull();
        return layout;
    }

    @Override
    public CanvasCssLayout createCssLayout(CssLayout cssLayoutModel) {
        CanvasCssLayout layout = components.create(CanvasCssLayout.class).init(cssLayoutModel);
        layout.setUuid(UUID.randomUUID());
        layout.setSizeFull();
        return layout;
    }

    @Override
    public CanvasGridLayout createCanvasGridLayout(GridLayout gridLayout) {
        CanvasGridLayout layout = components.create(CanvasGridLayout.class).init(gridLayout);
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
            CanvasWidgetLayout layout = components.create(CanvasWidgetLayout.class).init(widgetLayout);
            Label<String> label = components.create(Label.class);
            String message = messages.formatMessage(getClass(), "widgetNotFound", widget.getCaption(), widget.getName());
            label.setValue(message);
            layout.addComponent(label);
            log.error(message);
            return layout;
        }

        widget.setDashboard(frame.getDashboard());

        String frameId = widgetTypeOpt.get().getFrameId();
        Map<String, Object> params = new HashMap<>(ParamsMap.of(
                WIDGET, widget,
                DASHBOARD, frame.getDashboard(),
                DASHBOARD_FRAME, frame.getDashboardFrame()
        ));
        params.putAll(widgetRepository.getWidgetParams(widget));

        AbstractFrame widgetFrame = (AbstractFrame) appUI.getFragments().create(frame, frameId, new MapScreenOptions(params)).init();

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

        CanvasWidgetLayout layout = components.create(CanvasWidgetLayout.class).init(widgetLayout);
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
        CanvasRootLayout layout = components.create(CanvasRootLayout.class).init(rootLayout);
        layout.setUuid(UUID.randomUUID());
//        layout.setSizeFull();
        return layout;
    }


    @Override
    public CanvasResponsiveLayout createCanvasResponsiveLayout(ResponsiveLayout responsiveLayout) {
        CanvasResponsiveLayout layout = components.create(CanvasResponsiveLayout.class).init(responsiveLayout);
        layout.setUuid(UUID.randomUUID());
        layout.setSizeFull();
        return layout;
    }
}
