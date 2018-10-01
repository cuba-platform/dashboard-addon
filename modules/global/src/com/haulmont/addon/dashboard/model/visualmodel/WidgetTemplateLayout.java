/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.model.visualmodel;

import com.haulmont.chile.core.annotations.MetaClass;
import org.apache.commons.lang.StringUtils;

@MetaClass(name = "dashboard$WidgetTemplateLayout")
public class WidgetTemplateLayout extends WidgetLayout {

    @Override
    public String getCaption() {
        if (widget != null) {
            return widget.getCaption();
        }
        return StringUtils.EMPTY;
    }
}
