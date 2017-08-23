/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.widgetparameter;

import com.audimex.dashboard.entity.WidgetParameter;
import com.audimex.dashboard.entity.WidgetParameterType;
import com.haulmont.cuba.gui.components.AbstractEditor;

public class WidgetParameterEdit extends AbstractEditor<WidgetParameter> {
    @Override
    protected void initNewItem(WidgetParameter item) {
        super.initNewItem(item);

        item.setParameterType(WidgetParameterType.STRING);
    }
}