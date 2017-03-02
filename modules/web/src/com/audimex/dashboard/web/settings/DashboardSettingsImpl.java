/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.settings;

import com.audimex.dashboard.web.widgets.GridCell;
import com.vaadin.ui.*;
import org.vaadin.hene.popupbutton.PopupButton;

@org.springframework.stereotype.Component(DashboardSettings.NAME)
public class DashboardSettingsImpl implements DashboardSettings {
    @Override
    public boolean isComponentDraggable(Component component) {
        if (component instanceof Table) {
            return false;
        }
        if (component instanceof Field) {
            return false;
        }
        if (component instanceof Button) {
            return false;
        }
        if (component instanceof Tree) {
            return false;
        }
        if (component instanceof Embedded) {
            return false;
        }
        if (component instanceof Calendar) {
            return false;
        }
        if (component instanceof PopupButton) {
            return false;
        }
        if (component instanceof GridCell) {
            return false;
        }
        return true;
    }
}