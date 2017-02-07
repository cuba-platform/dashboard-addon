/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.settings;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Table;

public class DashboardSettingsImpl implements DashboardSettings {
    @Override
    public boolean isComponentDraggable(Component component) {
        //todo add more
        if (component instanceof Table) {
            return false;
        }
        if (component instanceof Field) {
            return false;
        }
        if (component instanceof Button) {
            return false;
        }
        return true;
    }
}