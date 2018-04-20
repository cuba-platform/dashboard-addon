/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.entity.parameter_types;

import javax.persistence.Embeddable;
import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.cuba.core.entity.EmbeddableEntity;
import javax.persistence.Column;

@MetaClass(name = "amxd$IntegerParameter")
@Embeddable
public class IntegerParameter extends EmbeddableEntity {
    private static final long serialVersionUID = -770060046691525893L;

    @Column(name = "INTEGER_VALUE")
    protected Integer integerValue;

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }


}