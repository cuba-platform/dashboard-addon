/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.sys;

import com.audimex.dashboard.web.DashboardSettings;
import com.audimex.dashboard.web.widgets.GridCell;
import com.haulmont.cuba.web.gui.components.WebPopupButton;
import com.vaadin.ui.*;

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
        if (component instanceof Embedded) {
            return false;
        }
        if (component instanceof Calendar) {
            return false;
        }
        if (component instanceof WebPopupButton) {
            return false;
        }
        if (component instanceof GridCell) {
            return false;
        }
        return true;
    }
}