/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.factory;

import com.audimex.dashboard.annotation_analyzer.WidgetTypeAnalyzer;
import com.audimex.dashboard.annotation_analyzer.WidgetTypeInfo;
import com.audimex.dashboard.gui.Draggable;
import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.model.visual_model.*;
import com.haulmont.addon.dnd.components.DDGridLayout;
import com.haulmont.addon.dnd.components.DDHorizontalLayout;
import com.haulmont.addon.dnd.components.DDVerticalLayout;
import com.haulmont.addon.dnd.components.DropHandler;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.App;
import com.vaadin.ui.Layout;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;

import static com.audimex.dashboard.web.widget_types.AbstractWidgetBrowse.WIDGET;

@Component
public abstract class LayoutFactory {
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected WidgetTypeAnalyzer typeAnalyzer;

    public com.haulmont.cuba.gui.components.Component createComponent(Draggable component) {
        DashboardLayout layout = component.getLayout();

        if (layout instanceof VerticalLayout) {
            return createVerticalLayout();
        } else if (layout instanceof HorizontalLayout) {
            return createHorizontalLayout();
        } else if (layout instanceof GridLayout) {
            return createGridLayout();
        } else if (layout instanceof GridArea) {
            return null;
//            return createGridArea();
        } else if (layout instanceof WidgetLayout) {
            return createWidgetLayout(((WidgetLayout) layout).getWidget());
        }
        return null;
    }

    public abstract DropHandler getVerticalDropHandler();

    public abstract DropHandler getHorizontalDropHandler();

    protected DDVerticalLayout createVerticalLayout() {
        DDVerticalLayout verticalLayout = componentsFactory.createComponent(DDVerticalLayout.class);
        verticalLayout.setMargin(true);
        verticalLayout.setSizeFull();
        verticalLayout.setStyleName("amxd-shadow-border");
        verticalLayout.setDropHandler(getVerticalDropHandler());
        return verticalLayout;
    }

    protected DDHorizontalLayout createHorizontalLayout() {
        DDHorizontalLayout horizontalLayout = componentsFactory.createComponent(DDHorizontalLayout.class);
        horizontalLayout.setMargin(true);
        horizontalLayout.setSizeFull();
        horizontalLayout.setStyleName("amxd-shadow-border");
        horizontalLayout.setDropHandler(getHorizontalDropHandler());
        return horizontalLayout;
    }


    protected com.haulmont.cuba.gui.components.Component createWidgetLayout(Widget widget) {
        Optional<WidgetTypeInfo> widgetTypeOpt = typeAnalyzer.getWidgetTypesInfo().stream()
                .filter(widgetType -> widget.getClass().equals(widgetType.getTypeClass()))
                .findFirst();

        if (widgetTypeOpt.isPresent()) {
            //todo: replace to inject ???
            WindowManager windowManager = App.getInstance().getWindowManager();
            WindowConfig windowConfig = AppBeans.get(WindowConfig.class);

            String frameId = widgetTypeOpt.get().getBrowseFrameId();
            WindowInfo windowInfo = windowConfig.getWindowInfo(frameId);

            //todo replace editWindow to real parent frame
            Window editWindow = windowManager.getOpenWindows().stream()
                    .filter(window -> "dashboardEdit".equals(window.getId()))
                    .findFirst()
                    .get();

            AbstractFrame widgetFrame = (AbstractFrame) windowManager.openFrame(
                    editWindow.getFrame(), null, windowInfo, ParamsMap.of(WIDGET, widget)
            );

            widgetFrame.setSizeFull();
            widgetFrame.setStyleName("amxd-widget-content");
            widgetFrame.setMargin(true);
            return widgetFrame;
        }

        return null;
    }

    protected DDGridLayout createGridLayout() {
        return null;
    }


}
