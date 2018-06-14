/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.tools.component_factories;

import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.web.dashboard.frames.editor.vaadin_components.PaletteButton;

public interface PaletteComponentsFactory {

    PaletteButton createVerticalLayoutButton();

    PaletteButton createHorizontalLayoutButton();

    PaletteButton createGridLayoutButton();

    PaletteButton createWidgetButton(Widget widget);
}
