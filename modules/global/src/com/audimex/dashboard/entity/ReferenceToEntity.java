/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.entity;

import javax.persistence.Embeddable;
import com.haulmont.chile.core.annotations.MetaClass;
import java.util.UUID;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.EmbeddableEntity;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;

@MetaClass(name = "amxd$ReferenceToEntity")
@Embeddable
public class ReferenceToEntity extends EmbeddableEntity {
    private static final long serialVersionUID = -6988978892111464242L;

    @Column(name = "ENTITY_ID")
    protected UUID entityId;

    @Column(name = "META_CLASS_NAME")
    protected String metaClassName;

    @Column(name = "VIEW_NAME")
    protected String viewName;

    public void setMetaClassName(String metaClassName) {
        this.metaClassName = metaClassName;
    }

    public String getMetaClassName() {
        return metaClassName;
    }


    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }


    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setObjectEntityId(Object referenceId) {
        if (referenceId instanceof UUID) {
            setEntityId((UUID) referenceId);
        } else {
            throw new IllegalArgumentException(
                    String.format("Unsupported primary key type: %s", referenceId.getClass().getSimpleName()));
        }
    }

}