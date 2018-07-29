/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.haulmont.addon.dashboard.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.List;

@NamePattern("%s|name")
@Table(name = "AMXD_WIDGET_TEMPLATE_GROUP")
@Entity(name = "amxd$WidgetTemplateGroup")
public class WidgetTemplateGroup extends StandardEntity {
    private static final long serialVersionUID = 6414734907756492318L;

    @NotNull
    @Column(name = "NAME", nullable = false, unique = true)
    protected String name;

    @OneToMany(mappedBy = "widgetTemplateGroup")
    protected List<WidgetTemplate> widgetTemplates;

    public void setWidgetTemplates(List<WidgetTemplate> widgetTemplates) {
        this.widgetTemplates = widgetTemplates;
    }

    public List<WidgetTemplate> getWidgetTemplates() {
        return widgetTemplates;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}