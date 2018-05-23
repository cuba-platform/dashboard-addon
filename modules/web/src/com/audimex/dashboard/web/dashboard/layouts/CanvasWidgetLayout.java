/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.layouts;

import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.ui.CssLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;
import fi.jasoft.dragdroplayouts.interfaces.DragFilter;

import java.util.Map;

public class CanvasWidgetLayout extends CssLayout implements CanvasLayout {


    @Override
    public DropHandler getDropHandler() {
        return null;
    }

    @Override
    public void setDropHandler(DropHandler dropHandler) {

    }

    @Override
    public TargetDetails translateDropTargetDetails(Map<String, Object> map) {
        return null;
    }

    @Override
    public LayoutDragMode getDragMode() {
        return null;
    }

    @Override
    public void setDragMode(LayoutDragMode layoutDragMode) {

    }

    @Override
    public DragFilter getDragFilter() {
        return null;
    }

    @Override
    public void setDragFilter(DragFilter dragFilter) {

    }

    @Override
    public Transferable getTransferable(Map<String, Object> map) {
        return null;
    }

}
