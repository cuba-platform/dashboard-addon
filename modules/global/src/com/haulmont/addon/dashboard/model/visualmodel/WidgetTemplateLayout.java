/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.model.visualmodel;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import org.apache.commons.lang.StringUtils;

import static java.lang.String.format;

@MetaClass(name = "dashboard$WidgetTemplateLayout")
public class WidgetTemplateLayout extends WidgetLayout {

    @Override
    public String getCaption() {
        if (widget != null) {
            Messages messages = AppBeans.get(Messages.class);
            String property = format("widgetType.%s", widget.getName());
            String mainMessage = messages.getMainMessage(property);
            String widgetType = (mainMessage.equals(property) ? widget.getName() : mainMessage);
            return widget.getCaption() + "(" + widgetType + ")";
        }
        return StringUtils.EMPTY;
    }
}
