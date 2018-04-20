/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.entity.parameter_types;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.Column;

@Table(name = "AMXD_LONG_PARAMETER")
@Entity(name = "amxd$LongParameter")
public class LongParameter extends StandardEntity {
    private static final long serialVersionUID = -1740013263728245766L;

    @Column(name = "LONG_VALUE")
    protected Long longValue;

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public Long getLongValue() {
        return longValue;
    }


}