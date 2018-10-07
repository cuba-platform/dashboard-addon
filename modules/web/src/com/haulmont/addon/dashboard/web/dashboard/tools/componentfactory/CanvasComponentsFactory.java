/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.tools.componentfactory;

import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasGridLayout;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasHorizontalLayout;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasVerticalLayout;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasWidgetLayout;

public interface CanvasComponentsFactory {

    CanvasVerticalLayout createCanvasVerticalLayout();

    CanvasHorizontalLayout createCanvasHorizontalLayout();

    CanvasGridLayout createCanvasGridLayout(int cols, int rows);

    CanvasWidgetLayout createCanvasWidgetLayout(CanvasFrame frame, Widget widget);

    CanvasVerticalLayout createCanvasRootLayout();
}
