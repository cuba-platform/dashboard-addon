/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.layouts;

import com.haulmont.addon.dnd.components.DDLayout;
import com.haulmont.addon.dnd.components.dragevent.DropTarget;
import com.haulmont.addon.dnd.components.dragfilter.DragFilterSupport;
import com.haulmont.addon.dnd.web.gui.components.TargetConverter;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.HBoxLayout;
import com.haulmont.cuba.web.gui.components.WebAbstractComponent;

public interface CanvasLayout extends Component, Component.Container, DDLayout, DragFilterSupport, DropTarget, TargetConverter, Component.LayoutClickNotifier, HasWeight {

    WebAbstractComponent getDelegate();

    HBoxLayout getButtonsPanel();

    void setDelegate(WebAbstractComponent delegate);

    void setButtonsPanel(HBoxLayout buttonsPanel);

}
