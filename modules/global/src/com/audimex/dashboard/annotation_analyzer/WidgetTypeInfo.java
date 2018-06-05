/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.annotation_analyzer;

import com.audimex.dashboard.annotation.WidgetType;

import java.io.Serializable;

/**
 * Contains values of annotation {@link WidgetType}, which is indicated class {@link WidgetTypeInfo#typeClass}
 */
public class WidgetTypeInfo implements Serializable {

    protected Class typeClass;
    protected String caption;
    protected String browseFrameId;
    protected String editFrameId;

    public WidgetTypeInfo() {
    }

    public WidgetTypeInfo(Class typeClass, String caption, String browseFrameId, String editFrameId) {
        this.typeClass = typeClass;
        this.caption = caption;
        this.browseFrameId = browseFrameId;
        this.editFrameId = editFrameId;
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

    public String getBrowseFrameId() {
        return browseFrameId;
    }

    public void setBrowseFrameId(String browseFrameId) {
        this.browseFrameId = browseFrameId;
    }

    public String getEditFrameId() {
        return editFrameId;
    }

    public void setEditFrameId(String editFrameId) {
        this.editFrameId = editFrameId;
    }
}
