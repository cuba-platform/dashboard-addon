/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "AMXD_DASHBOARD_WIDGET")
@Entity(name = "amxd$DashboardWidget")
public class DashboardWidget extends StandardEntity {
    private static final long serialVersionUID = 8608098106185215266L;

    @Column(name = "WIDGET_ID")
    protected String widgetId;

    @Column(name = "CAPTION")
    protected String caption;

    @Column(name = "ICON")
    protected String icon;

    @Column(name = "DESCRIPTION")
    protected String description;

    @Column(name = "FRAME_ID")
    protected String frameId;

    public void setWidgetId(String widgetId) {
        this.widgetId = widgetId;
    }

    public String getWidgetId() {
        return widgetId;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setFrameId(String frameId) {
        this.frameId = frameId;
    }

    public String getFrameId() {
        return frameId;
    }


}