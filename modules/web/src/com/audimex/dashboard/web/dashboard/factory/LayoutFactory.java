/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.factory;

import com.audimex.dashboard.annotation_analyzer.WidgetTypeAnalyzer;
import com.audimex.dashboard.annotation_analyzer.WidgetTypeInfo;
import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.model.visual_model.*;
import com.haulmont.addon.dnd.components.DDGridLayout;
import com.haulmont.addon.dnd.components.DDHorizontalLayout;
import com.haulmont.addon.dnd.components.DDVerticalLayout;
import com.haulmont.addon.dnd.components.DropHandler;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.Component.Container;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;
import java.util.Optional;

import static com.audimex.dashboard.web.widget_types.AbstractWidgetBrowse.WIDGET;

@org.springframework.stereotype.Component
@Scope("prototype")
public abstract class LayoutFactory {
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected WidgetTypeAnalyzer typeAnalyzer;

    //todo: hack, replace to something else
    protected Frame parentFrame;

    public void setParentFrame(Frame parentFrame) {
        this.parentFrame = parentFrame;
    }

    public Container createContainer(DashboardLayout layout) {
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


    protected Container createWidgetLayout(Widget widget) {
        Optional<WidgetTypeInfo> widgetTypeOpt = typeAnalyzer.getWidgetTypesInfo().stream()
                .filter(widgetType -> widget.getClass().equals(widgetType.getTypeClass()))
                .findFirst();

        if (widgetTypeOpt.isPresent()) {
            String frameId = widgetTypeOpt.get().getBrowseFrameId();
            Frame widgetFrame = parentFrame.openFrame(null, frameId, ParamsMap.of(WIDGET, widget));

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
