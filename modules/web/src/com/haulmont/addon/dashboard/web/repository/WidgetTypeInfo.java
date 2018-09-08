/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.repository;

import java.io.Serializable;

/**
 * Contains values of annotation {@link WidgetType}, which is indicated class {@link WidgetTypeInfo#typeClass}
 */
public class WidgetTypeInfo implements Serializable {

    protected String name;
    protected String browseFrameId;
    protected String editFrameId;

    public WidgetTypeInfo() {
    }

    public WidgetTypeInfo(String name, String browseFrameId, String editFrameId) {
        this.name = name;
        this.browseFrameId = browseFrameId;
        this.editFrameId = editFrameId;
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
