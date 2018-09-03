/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.tools.component_factories;

import com.haulmont.addon.dashboard.model.Parameter;
import com.haulmont.addon.dashboard.web.annotation_analyzer.WidgetRepository;
import com.haulmont.addon.dashboard.web.annotation_analyzer.WidgetTypeInfo;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.web.DashboardException;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasGridLayout;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasHorizontalLayout;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasVerticalLayout;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasWidgetLayout;
import com.haulmont.addon.dashboard.web.widget_types.AbstractWidgetBrowse;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractFrame;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.*;

import static com.haulmont.addon.dashboard.web.widget_types.AbstractWidgetBrowse.DASHBOARD;
import static com.haulmont.addon.dashboard.web.widget_types.AbstractWidgetBrowse.WIDGET;
import static com.haulmont.addon.dnd.components.enums.LayoutDragMode.NONE;
import static java.lang.String.format;

@Component("amdx_VaadinUiComponentsFactory")
public class CanvasUiComponentsFactory implements CanvasComponentsFactory {
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

        String frameId = widgetTypeOpt.get().getBrowseFrameId();
        Map<String, Object> params = new HashMap<>(ParamsMap.of(
                WIDGET, widget,
                DASHBOARD, frame.getDashboard()
        ));
        params.putAll(widgetRepository.getWidgetParams(widget));

        AbstractFrame widgetFrame = frame.openFrame(null, frameId, params);
//        widgetRepository.initializeWidgetFields(widgetFrame, widget);

        widgetFrame.setSizeFull();
        widgetFrame.setMargin(true);

        CanvasWidgetLayout layout = new CanvasWidgetLayout();
        layout.setUuid(UUID.randomUUID());
        layout.getDelegate().add(widgetFrame);
        layout.setWidget(widget);
        layout.setDragMode(NONE);
        layout.setSizeFull();
        return layout;
    }
}
