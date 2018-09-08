/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.model.visualmodel;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;

@MetaClass(name = "dashboard$HorizontalLayout")
public class HorizontalLayout extends DashboardLayout implements ContainerLayout {

    @Override
    public String getCaption() {
        Messages messages = AppBeans.get(Messages.class);
        return messages.getMessage(getClass(), "Layout.horizontal");
    }
}