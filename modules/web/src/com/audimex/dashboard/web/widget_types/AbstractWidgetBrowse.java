/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.widget_types;

import com.audimex.dashboard.model.Parameter;
import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.web.dashboard.drop_handlers.NotDropHandler;
import com.haulmont.addon.dnd.components.DDLayout;
import com.haulmont.addon.dnd.components.DropHandler;
import com.haulmont.addon.dnd.components.enums.LayoutDragMode;
import com.haulmont.cuba.gui.components.AbstractFrame;

import java.util.Map;
import java.util.stream.Collectors;

public class AbstractWidgetBrowse extends AbstractFrame implements DDLayout {
    public static final String WIDGET = "WIDGET";

    protected Widget widget;
    protected LayoutDragMode dragMode = LayoutDragMode.CLONE;
    protected DropHandler dropHandler = new NotDropHandler();

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        widget = (Widget) params.get(WIDGET);

        if (widget == null) {
            //todo create DashboardException
            throw new RuntimeException("Can't get a Widget object in input parameters");
        }
    }

    public Map<String, Object> getParamsForFrame() {
        return widget.getParameters().stream()
                .collect(Collectors.toMap(
                        Parameter::getName,
                        parameter -> parameter.getParameterValue().getValue()
                ));
    }

    public Widget getWidget() {
        return widget;
    }

    @Override
    public void setDragMode(LayoutDragMode startMode) {
        dragMode = startMode;
    }

    @Override
    public LayoutDragMode getDragMode() {
        return dragMode;
    }

    @Override
    public void setDropHandler(DropHandler handler) {
        dropHandler = handler;
    }

    @Override
    public DropHandler getDropHandler() {
        return this.dropHandler;
    }
}
