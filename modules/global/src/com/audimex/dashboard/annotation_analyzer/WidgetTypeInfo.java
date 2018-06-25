/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.annotation_analyzer;

import com.audimex.dashboard.annotation.WidgetType;
import com.audimex.dashboard.model.Widget;

import java.io.Serializable;

/**
 * Contains values of annotation {@link WidgetType}, which is indicated class {@link WidgetTypeInfo#typeClass}
 */
public class WidgetTypeInfo<T extends Widget> implements Serializable {

    protected Class<T> typeClass;
    protected String name;
    protected String browseFrameId;
    protected String editFrameId;

    public WidgetTypeInfo() {
    }

    public WidgetTypeInfo(Class<T> typeClass, String name, String browseFrameId, String editFrameId) {
        this.typeClass = typeClass;
        this.name = name;
        this.browseFrameId = browseFrameId;
        this.editFrameId = editFrameId;
    }

    public Class<T> getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(Class<T> typeClass) {
        this.typeClass = typeClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
