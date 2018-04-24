/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.model;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.chile.core.annotations.NamePattern;

import java.util.List;

@NamePattern("%s|caption")
@MetaClass(name = "amxd$Widget")
public class Widget extends BaseUuidEntity implements HasParameters {
    private static final long serialVersionUID = 2343551192474818297L;

    @MetaProperty
    protected String caption;
    @MetaProperty
    protected String icon;
    @MetaProperty
    protected String description;
    @MetaProperty
    protected String frameId;
    @MetaProperty
    protected Boolean isTemplate;
    @MetaProperty
    protected List<Parameter> parameters;

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    @Override
    public List<Parameter> getParameters() {
        return parameters;
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

    public void setIsTemplate(Boolean isTemplate) {
        this.isTemplate = isTemplate;
    }

    public Boolean getIsTemplate() {
        return isTemplate;
    }


}