/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.model.visualmodel;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;

@MetaClass(name = "dashboard$CssLayout")
public class CssLayout extends DashboardLayout implements ContainerLayout {

    @MetaProperty
    protected Boolean responsive;

    @Override
    public String getCaption() {
        Messages messages = AppBeans.get(Messages.class);
        return messages.getMessage(getClass(), "Layout.css");
    }

    public Boolean getResponsive() {
        return responsive;
    }

    public void setResponsive(Boolean responsive) {
        this.responsive = responsive;
    }
}
