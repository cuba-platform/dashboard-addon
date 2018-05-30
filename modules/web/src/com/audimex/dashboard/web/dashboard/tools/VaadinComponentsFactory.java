/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.tools;

import com.audimex.dashboard.model.Dashboard;
import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.web.dashboard.vaadin_components.layouts.CanvasGridLayout;
import com.audimex.dashboard.web.dashboard.vaadin_components.layouts.CanvasHorizontalLayout;
import com.audimex.dashboard.web.dashboard.vaadin_components.layouts.CanvasVerticalLayout;
import com.audimex.dashboard.web.dashboard.vaadin_components.layouts.CanvasWidgetLayout;
import com.haulmont.cuba.gui.components.Frame;

public interface VaadinComponentsFactory {

    CanvasVerticalLayout createCanvasVerticalLayout();

    CanvasHorizontalLayout createCanvasHorizontalLayout();

    CanvasGridLayout createCanvasGridLayout(int cols, int rows);

    CanvasWidgetLayout createCanvasWidgetLayout(Frame targetFrame, Widget widget, Dashboard dashboard);
}
