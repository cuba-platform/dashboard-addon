/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.entity;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@NamePattern("%s|caption")
@Table(name = "AMXD_WIDGET")
@Entity(name = "amxd$Widget")
public class Widget extends StandardEntity {
    private static final long serialVersionUID = 8608098106185215266L;

    @NotNull
    @Column(name = "WIDGET_ID", nullable = false)
    protected String widgetId;

    @NotNull
    @Column(name = "CAPTION", nullable = false)
    protected String caption;

    @Column(name = "ICON")
    protected String icon;

    @Column(name = "DESCRIPTION")
    protected String description;

    @NotNull
    @Column(name = "FRAME_ID", nullable = false)
    protected String frameId;

    @Column(name = "IS_TEMPLATE")
    protected Boolean isTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DASHBOARD_ID")
    protected Dashboard dashboard;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "widget")
    protected List<Parameter> parameters;

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public void setIsTemplate(Boolean isTemplate) {
        this.isTemplate = isTemplate;
    }

    public Boolean getIsTemplate() {
        return isTemplate;
    }

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