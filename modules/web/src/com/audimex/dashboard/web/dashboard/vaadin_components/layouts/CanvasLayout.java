/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.vaadin_components.layouts;

import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.ui.Component;
import fi.jasoft.dragdroplayouts.interfaces.LayoutDragSource;

public interface CanvasLayout extends Component, LayoutDragSource, DropTarget {

    void setDropHandler(DropHandler dropHandler);
}
