/*
 * Copyright (c) 2008-2018 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
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
