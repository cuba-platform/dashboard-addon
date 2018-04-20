/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.entity.parameter_types;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.cuba.core.entity.EmbeddableEntity;

import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@MetaClass(name = "amxd$DateParameter")
@Embeddable
public class DateParameter extends EmbeddableEntity {
    private static final long serialVersionUID = 5580916749019606721L;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_VALUE")
    protected Date dateValue;

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }
}