/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import java.util.List;

@NamePattern("%s|title")
@Table(name = "AMXD_DASHBOARD")
@Entity(name = "amxd$Dashboard")
public class Dashboard extends StandardEntity {
    private static final long serialVersionUID = 8876942042181481797L;

    @Column(name = "TITLE")
    protected String title;

    @Lob
    @Column(name = "VISUAL_MODEL")
    protected String visualModel;

    @Column(name = "IS_TEMPLATE")
    protected Boolean isTemplate;

    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "dashboard")
    protected List<Widget> widgets;

    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "dashboard")
    protected List<Parameter> parameters;

    public void setWidgets(List<Widget> widgets) {
        this.widgets = widgets;
    }

    public List<Widget> getWidgets() {
        return widgets;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setIsTemplate(Boolean isTemplate) {
        this.isTemplate = isTemplate;
    }

    public Boolean getIsTemplate() {
        return isTemplate;
    }

    public void setVisualModel(String visualModel) {
        this.visualModel = visualModel;
    }

    public String getVisualModel() {
        return visualModel;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}