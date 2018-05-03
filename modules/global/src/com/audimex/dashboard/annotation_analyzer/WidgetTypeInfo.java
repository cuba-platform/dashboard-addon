/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.annotation_analyzer;

import java.io.Serializable;

public class WidgetTypeInfo implements Serializable {

    protected Class typeClass;
    protected String caption;
    protected Class browseClass;
    protected Class editClass;

    public WidgetTypeInfo() {
    }

    public WidgetTypeInfo(Class typeClass, String caption, Class browseClass, Class editClass) {
        this.typeClass = typeClass;
        this.caption = caption;
        this.browseClass = browseClass;
        this.editClass = editClass;
    }

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

    public Class getBrowseClass() {
        return browseClass;
    }

    public void setBrowseClass(Class browseClass) {
        this.browseClass = browseClass;
    }

    public Class getEditClass() {
        return editClass;
    }

    public void setEditClass(Class editClass) {
        this.editClass = editClass;
    }
}
