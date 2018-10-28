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
import com.haulmont.addon.dashboard.web.DashboardException;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasGridLayout;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasHorizontalLayout;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasVerticalLayout;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasWidgetLayout;
import com.haulmont.addon.dashboard.web.repository.WidgetRepository;
import com.haulmont.addon.dashboard.web.repository.WidgetTypeInfo;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.haulmont.addon.dnd.components.enums.LayoutDragMode.NONE;
import static java.lang.String.format;

@Component("dashboard_uiComponentsFactory")
public class CanvasUiComponentsFactory implements CanvasComponentsFactory {

    public static final String WIDGET = "widget";
    public static final String DASHBOARD = "dashboard";
    public static final String DASHBOARD_FRAME = "dashboardFrame";

    @Inject
    protected WidgetRepository widgetRepository;

    @Inject
    protected ComponentsFactory componentsFactory;

    @Override
    public CanvasVerticalLayout createCanvasVerticalLayout() {
        CanvasVerticalLayout layout = new CanvasVerticalLayout();
        layout.setUuid(UUID.randomUUID());
        layout.setDragMode(NONE);
        layout.setSizeFull();
        return layout;
    }

    @Override
    public CanvasHorizontalLayout createCanvasHorizontalLayout() {
        CanvasHorizontalLayout layout = new CanvasHorizontalLayout();
        layout.setUuid(UUID.randomUUID());
        layout.setDragMode(NONE);
        layout.setSizeFull();
        return layout;
    }

    @Override
    public CanvasGridLayout createCanvasGridLayout(int cols, int rows) {
        CanvasGridLayout layout = new CanvasGridLayout(cols, rows);
        layout.setUuid(UUID.randomUUID());
        layout.setDragMode(NONE);
        layout.setSizeFull();
        return layout;
    }

    @Override
    public CanvasWidgetLayout createCanvasWidgetLayout(CanvasFrame frame, Widget widget) {
        Optional<WidgetTypeInfo> widgetTypeOpt = widgetRepository.getWidgetTypesInfo().stream()
                .filter(widgetType -> widget.getBrowseFrameId().equals(widgetType.getBrowseFrameId()))
                .findFirst();

        if (!widgetTypeOpt.isPresent()) {
            throw new DashboardException(format("There isn't found a screen for the widget %s", widget.getBrowseFrameId()));
        }

        widget.setDashboard(frame.getDashboard());

        String frameId = widgetTypeOpt.get().getBrowseFrameId();
        Map<String, Object> params = new HashMap<>(ParamsMap.of(
                WIDGET, widget,
                DASHBOARD, frame.getDashboard(),
                DASHBOARD_FRAME, frame.getDashboardFrame()
        ));
        params.putAll(widgetRepository.getWidgetParams(widget));

        AbstractFrame widgetFrame = frame.openFrame(null, frameId, params);

        widgetFrame.setSizeFull();


        VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
        vBoxLayout.setSpacing(true);
        vBoxLayout.setMargin(true);
        vBoxLayout.setSizeFull();

        if (BooleanUtils.isTrue(widget.getShowWidgetCaption())){
            Label label = componentsFactory.createComponent(Label.class);
            label.setValue(widget.getCaption());
            label.setStyleName("h2");
            vBoxLayout.add(label);
        }

        vBoxLayout.add(widgetFrame);
        vBoxLayout.expand(widgetFrame);

        CanvasWidgetLayout layout = new CanvasWidgetLayout();
        layout.setUuid(UUID.randomUUID());
        layout.getDelegate().add(vBoxLayout);
        layout.setWidget(widget);
        layout.setDragMode(NONE);
        layout.setSizeFull();
        return layout;
    }

    @Override
    public CanvasVerticalLayout createCanvasRootLayout() {
        CanvasVerticalLayout layout = new CanvasVerticalLayout();
        layout.setUuid(UUID.randomUUID());
        layout.setDragMode(NONE);
        layout.setSizeFull();
        return layout;
    }
}
