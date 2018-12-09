package com.haulmont.addon.dashboard.model.visualmodel;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;

@MetaClass(name = "dashboard$ResponsiveLayout")
public class DashboardResponsiveLayout extends DashboardLayout implements ContainerLayout {//

    @MetaProperty
    protected Integer columns;

    @Override
    public String getCaption() {
        Messages messages = AppBeans.get(Messages.class);
        return messages.getMessage(getClass(), "Layout.css");
    }

    public Integer getColumns() {
        return columns;
    }

    public void setColumns(Integer columns) {
        this.columns = columns;
    }
}
