/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.tools.componentfactory;

import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.web.DashboardException;
import com.haulmont.addon.dashboard.web.repository.WidgetRepository;
import com.haulmont.addon.dashboard.web.repository.WidgetTypeInfo;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasGridLayout;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasHorizontalLayout;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasVerticalLayout;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasWidgetLayout;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.ScrollBoxLayout;
import com.haulmont.cuba.web.gui.components.WebScrollBoxLayout;
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
        widgetFrame.setMargin(true);

        ScrollBoxLayout scrollBoxLayout = new WebScrollBoxLayout();
        scrollBoxLayout.add(widgetFrame);
        scrollBoxLayout.setWidth("100%");
        scrollBoxLayout.setHeight("100%");

        CanvasWidgetLayout layout = new CanvasWidgetLayout();
        layout.setUuid(UUID.randomUUID());
        layout.getDelegate().add(scrollBoxLayout);
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
