/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.tools.component_factories;

import com.haulmont.addon.dashboard.annotation_analyzer.WidgetTypeAnalyzer;
import com.haulmont.addon.dashboard.annotation_analyzer.WidgetTypeInfo;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.web.DashboardException;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasGridLayout;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasHorizontalLayout;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasVerticalLayout;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasWidgetLayout;
import com.haulmont.addon.dashboard.web.widget_types.AbstractWidgetBrowse;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.addon.dashboard.web.DashboardException;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

import static com.haulmont.addon.dashboard.web.widget_types.AbstractWidgetBrowse.DASHBOARD;
import static com.haulmont.addon.dashboard.web.widget_types.AbstractWidgetBrowse.WIDGET;
import static com.haulmont.addon.dnd.components.enums.LayoutDragMode.NONE;
import static java.lang.String.format;

@Component("amdx_VaadinUiComponentsFactory")
public class CanvasUiComponentsFactory implements CanvasComponentsFactory {
    @Inject
    protected WidgetTypeAnalyzer typeAnalyzer;

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
        Optional<WidgetTypeInfo> widgetTypeOpt = typeAnalyzer.getWidgetTypesInfo().stream()
                .filter(widgetType -> widget.getClass().equals(widgetType.getTypeClass()))
                .findFirst();

        if (!widgetTypeOpt.isPresent()) {
            throw new DashboardException(format("There isn't found a screen for the widget class %s", widget.getClass()));
        }

        String frameId = widgetTypeOpt.get().getBrowseFrameId();
        AbstractWidgetBrowse widgetFrame = (AbstractWidgetBrowse) frame.openFrame(null, frameId, ParamsMap.of(
                WIDGET, widget,
                DASHBOARD, frame.getDashboard()
        ));
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
