/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.haulmont.addon.dashboard.entity;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *  Persistence Widget Template. The field {@link WidgetTemplate#widgetModel} stores not persistence model
 *  {@link com.haulmont.addon.dashboard.model.Widget} as JSON
 */
@Table(name = "DASHBOARD_WIDGET_TEMPLATE")
@Entity(name = "dashboard$WidgetTemplate")
public class WidgetTemplate extends StandardEntity {
    private static final long serialVersionUID = -2121278399449430493L;

    @NotNull
    @Lob
    @Column(name = "WIDGET_MODEL", nullable = false)
    protected String widgetModel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WIDGET_TEMPLATE_GROUP_ID")
    protected WidgetTemplateGroup widgetTemplateGroup;

    public void setWidgetTemplateGroup(WidgetTemplateGroup widgetTemplateGroup) {
        this.widgetTemplateGroup = widgetTemplateGroup;
    }

    public WidgetTemplateGroup getWidgetTemplateGroup() {
        return widgetTemplateGroup;
    }

    public void setWidgetModel(String widgetModel) {
        this.widgetModel = widgetModel;
    }

    public String getWidgetModel() {
        return widgetModel;
    }


}