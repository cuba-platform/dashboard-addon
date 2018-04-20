/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.entity.parameter_types;

import javax.persistence.Embeddable;
import com.haulmont.chile.core.annotations.MetaClass;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.EmbeddableEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@MetaClass(name = "amxd$StringParameter")
@Embeddable
public class StringParameter extends EmbeddableEntity {
    private static final long serialVersionUID = -1610732374640762738L;

    @Column(name = "STRING_VALUE")
    protected String stringValue;

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }


}