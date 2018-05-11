/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.model;

import com.audimex.dashboard.model.visual_model.VerticalLayout;
import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.chile.core.annotations.NamePattern;

import java.util.List;

@NamePattern("%s|title")
@MetaClass(name = "amxd$Dashboard")
public class Dashboard extends BaseUuidEntity {
    @MetaProperty
    protected String title;
    @MetaProperty
    protected VerticalLayout visualModel;
    @MetaProperty
    protected List<Parameter> parameters;

    public VerticalLayout getVisualModel() {
        return visualModel;
    }

    public void setVisualModel(VerticalLayout visualModel) {
        this.visualModel = visualModel;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

}