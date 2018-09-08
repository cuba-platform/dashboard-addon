/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.tools.componentfactory;

import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.components.PaletteButton;

public interface PaletteComponentsFactory {

    PaletteButton createVerticalLayoutButton();

    PaletteButton createHorizontalLayoutButton();

    PaletteButton createGridLayoutButton();

    PaletteButton createWidgetButton(Widget widget);
}
