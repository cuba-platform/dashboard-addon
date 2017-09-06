/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.entity;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.reports.entity.Report;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@NamePattern("%s|caption")
@Table(name = "AMXD_DASHBOARD_WIDGET")
@Entity(name = "amxd$DashboardWidget")
public class DashboardWidget extends StandardEntity {
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

    @Column(name = "WIDGET_VIEW_TYPE")
    protected Integer widgetViewType;

    @Column(name = "ENTITY_TYPE")
    protected String entityType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORT_ID")
    protected Report report;

    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "dashboardWidget")
    protected List<DashboardWidgetLink> dashboardLinks;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "dashboardWidget")
    protected List<WidgetParameter> parameters;

    @Column(name = "IS_TEMPLATE")
    protected Boolean isTemplate;

    @Transient
    protected com.haulmont.cuba.core.entity.Entity entity;

    public void setIsTemplate(Boolean isTemplate) {
        this.isTemplate = isTemplate;
    }

    public Boolean getIsTemplate() {
        return isTemplate;
    }


    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }


    public void setParameters(List<WidgetParameter> parameters) {
        this.parameters = parameters;
    }

    public List<WidgetParameter> getParameters() {
        return parameters;
    }


    public List<DashboardWidgetLink> getDashboardLinks() {
        return dashboardLinks == null ? new ArrayList<>() : dashboardLinks;
    }

    public void addDashboardLink(DashboardWidgetLink dashboardLink) {
        if (dashboardLinks == null) {
            dashboardLinks = new ArrayList<>();
        }
        dashboardLinks.add(dashboardLink);
    }

    public void setDashboardLinks(List<DashboardWidgetLink> dashboardLinks) {
        this.dashboardLinks = dashboardLinks;
    }


    public void setWidgetViewType(WidgetViewType widgetViewType) {
        this.widgetViewType = widgetViewType == null ? null : widgetViewType.getId();
    }

    public WidgetViewType getWidgetViewType() {
        return widgetViewType == null ? null : WidgetViewType.fromId(widgetViewType);
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityType() {
        return entityType;
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

    public com.haulmont.cuba.core.entity.Entity getEntity() {
        return entity;
    }

    public void setEntity(com.haulmont.cuba.core.entity.Entity entity) {
        this.entity = entity;
    }

}