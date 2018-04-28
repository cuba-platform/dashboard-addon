/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.model;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import java.util.List;

import com.audimex.dashboard.model.visual_model.DashboardVisualModel;

@NamePattern("%s|title")
@MetaClass(name = "amxd$Dashboard")
public class Dashboard extends BaseUuidEntity {
    private static final long serialVersionUID = -3608201761546718634L;

    @MetaProperty
    protected String title;
    @MetaProperty
    protected List<Widget> widgets;
    @MetaProperty
    protected DashboardVisualModel visualModel;
    @MetaProperty
    protected List<Parameter> parameters;

    public void setVisualModel(DashboardVisualModel visualModel) {
        this.visualModel = visualModel;
    }

    public DashboardVisualModel getVisualModel() {
        return visualModel;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public List<Widget> getWidgets() {
        return widgets;
    }

    public void setWidgets(List<Widget> widgets) {
        this.widgets = widgets;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

}