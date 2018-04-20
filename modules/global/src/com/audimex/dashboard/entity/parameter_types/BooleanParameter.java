/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.entity.parameter_types;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.cuba.core.entity.EmbeddableEntity;

@MetaClass(name = "amxd$BooleanParameter")
@Embeddable
public class BooleanParameter extends EmbeddableEntity {
    private static final long serialVersionUID = 8862760799735087229L;

    @Column(name = "BOOLEAN_VALUE")
    protected Boolean booleanValue;

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }
}