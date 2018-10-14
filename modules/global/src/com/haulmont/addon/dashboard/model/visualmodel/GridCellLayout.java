package com.haulmont.addon.dashboard.model.visualmodel;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;

import javax.validation.constraints.NotNull;

@MetaClass(name = "dashboard$GridCellLayout")
public class GridCellLayout extends VerticalLayout {

    @NotNull
    @MetaProperty(mandatory = true)
    protected Integer row = 0;

    @NotNull
    @MetaProperty(mandatory = true)
    protected Integer column = 0;

    @Override
    public String getCaption() {
        Messages messages = AppBeans.get(Messages.class);
        return messages.formatMessage(this.getClass(), "Layout.gridCell", row, column);
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }
}
