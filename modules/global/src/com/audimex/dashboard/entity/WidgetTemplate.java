/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Lob;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "AMXD_WIDGET_TEMPLATE")
@Entity(name = "amxd$WidgetTemplate")
public class WidgetTemplate extends StandardEntity {
    private static final long serialVersionUID = -2121278399449430493L;

    @Lob
    @Column(name = "WIDGET_MODEL")
    protected String widgetModel;

    public void setWidgetModel(String widgetModel) {
        this.widgetModel = widgetModel;
    }

    public String getWidgetModel() {
        return widgetModel;
    }


}