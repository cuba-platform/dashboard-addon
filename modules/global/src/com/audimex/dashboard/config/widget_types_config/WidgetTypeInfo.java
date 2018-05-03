/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.config.widget_types_config;

import java.io.Serializable;

public class WidgetTypeInfo implements Serializable {

    protected Class typeClass;
    protected String caption;
    protected String browseClass;
    protected String editClass;

    public Class getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(Class typeClass) {
        this.typeClass = typeClass;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getBrowseClass() {
        return browseClass;
    }

    public void setBrowseClass(String browseClass) {
        this.browseClass = browseClass;
    }

    public String getEditClass() {
        return editClass;
    }

    public void setEditClass(String editClass) {
        this.editClass = editClass;
    }
}
