/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.tools;

import com.audimex.dashboard.annotation_analyzer.WidgetTypeAnalyzer;
import com.audimex.dashboard.annotation_analyzer.WidgetTypeInfo;
import com.audimex.dashboard.model.Widget;
import com.haulmont.addon.dnd.components.DDGridLayout;
import com.haulmont.addon.dnd.components.DDHorizontalLayout;
import com.haulmont.addon.dnd.components.DDVerticalLayout;
import com.haulmont.addon.dnd.components.enums.LayoutDragMode;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.Component.Container;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;

import static com.audimex.dashboard.web.DashboardStyleConstants.AMXD_SHADOW_BORDER;
import static com.audimex.dashboard.web.DashboardStyleConstants.AMXD_WIDGET_CONTENT;
import static com.audimex.dashboard.web.widget_types.AbstractWidgetBrowse.WIDGET;

@Component
public class LayoutFactory {
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected WidgetTypeAnalyzer typeAnalyzer;

    public DDVerticalLayout createVerticalLayout() {
        DDVerticalLayout verticalLayout = componentsFactory.createComponent(DDVerticalLayout.class);
        verticalLayout.setMargin(true);
        verticalLayout.setSizeFull();
        verticalLayout.addStyleName(AMXD_SHADOW_BORDER);
        verticalLayout.setDragMode(LayoutDragMode.CLONE);
        return verticalLayout;
    }

    public DDHorizontalLayout createHorizontalLayout() {
        DDHorizontalLayout horizontalLayout = componentsFactory.createComponent(DDHorizontalLayout.class);
        horizontalLayout.setMargin(true);
        horizontalLayout.setSizeFull();
        horizontalLayout.addStyleName(AMXD_SHADOW_BORDER);
        horizontalLayout.setDragMode(LayoutDragMode.CLONE);
        return horizontalLayout;
    }


    public Container createWidgetLayout(Widget widget, Frame parentFrame) {
        Optional<WidgetTypeInfo> widgetTypeOpt = typeAnalyzer.getWidgetTypesInfo().stream()
                .filter(widgetType -> widget.getClass().equals(widgetType.getTypeClass()))
                .findFirst();

        if (widgetTypeOpt.isPresent()) {
            String frameId = widgetTypeOpt.get().getBrowseFrameId();
            Frame widgetFrame = parentFrame.openFrame(null, frameId, ParamsMap.of(WIDGET, widget));

            widgetFrame.setSizeFull();
            widgetFrame.addStyleName(AMXD_WIDGET_CONTENT);
            widgetFrame.setMargin(true);
            return widgetFrame;
        }

        return null;
    }

    public DDGridLayout createGridLayout(int cols, int rows) {
        DDGridLayout container = componentsFactory.createComponent(DDGridLayout.class);
        container.setColumns(cols);
        container.setRows(rows);
        container.setMargin(true);
        container.setSizeFull();
        container.addStyleName(AMXD_SHADOW_BORDER);
        container.setDragMode(LayoutDragMode.CLONE);

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                container.add(createVerticalLayout(), i, j);
            }
        }

        return container;
    }


}
