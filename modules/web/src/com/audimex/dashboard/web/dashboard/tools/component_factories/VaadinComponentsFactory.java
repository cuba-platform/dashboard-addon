/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.tools.component_factories;

import com.audimex.dashboard.model.Dashboard;
import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.web.dashboard.layouts.CanvasGridLayout;
import com.audimex.dashboard.web.dashboard.layouts.CanvasHorizontalLayout;
import com.audimex.dashboard.web.dashboard.layouts.CanvasVerticalLayout;
import com.audimex.dashboard.web.dashboard.layouts.CanvasWidgetLayout;
import com.haulmont.cuba.gui.components.Frame;

public interface VaadinComponentsFactory {

    CanvasVerticalLayout createCanvasVerticalLayout();

    CanvasHorizontalLayout createCanvasHorizontalLayout();

    CanvasGridLayout createCanvasGridLayout(int cols, int rows);

    CanvasWidgetLayout createCanvasWidgetLayout(Frame targetFrame, Widget widget, Dashboard dashboard);
}
