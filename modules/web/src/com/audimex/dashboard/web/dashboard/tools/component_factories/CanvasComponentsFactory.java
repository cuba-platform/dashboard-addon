/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.tools.component_factories;

import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame;
import com.audimex.dashboard.web.dashboard.layouts.CanvasGridLayout;
import com.audimex.dashboard.web.dashboard.layouts.CanvasHorizontalLayout;
import com.audimex.dashboard.web.dashboard.layouts.CanvasVerticalLayout;
import com.audimex.dashboard.web.dashboard.layouts.CanvasWidgetLayout;

public interface CanvasComponentsFactory {

    CanvasVerticalLayout createCanvasVerticalLayout();

    CanvasHorizontalLayout createCanvasHorizontalLayout();

    CanvasGridLayout createCanvasGridLayout(int cols, int rows);

    CanvasWidgetLayout createCanvasWidgetLayout(CanvasFrame frame, Widget widget);
}
