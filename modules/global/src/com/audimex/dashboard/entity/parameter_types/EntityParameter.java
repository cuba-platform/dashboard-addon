/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.entity.parameter_types;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.cuba.core.entity.EmbeddableEntity;

import java.util.UUID;

@MetaClass(name = "amxd$EntityParameter")
@Embeddable
public class EntityParameter extends EmbeddableEntity {
    private static final long serialVersionUID = 3015011186689912524L;

    @Column(name = "ENTITY_ID")
    protected UUID entityId;

    @Column(name = "META_CLASS_NAME")
    protected String metaClassName;

    @Column(name = "VIEW_NAME")
    protected String viewName;

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public String getMetaClassName() {
        return metaClassName;
    }

    public void setMetaClassName(String metaClassName) {
        this.metaClassName = metaClassName;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
}