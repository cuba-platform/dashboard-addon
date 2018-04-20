/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.entity.parameter_types;

import javax.persistence.Embeddable;
import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.cuba.core.entity.EmbeddableEntity;
import java.math.BigDecimal;
import javax.persistence.Column;

@MetaClass(name = "amxd$DecimalParameter")
@Embeddable
public class DecimalParameter extends EmbeddableEntity {
    private static final long serialVersionUID = -8244711050524997217L;

    @Column(name = "DECIMAL_VALUE")
    protected BigDecimal decimalValue;

    public void setDecimalValue(BigDecimal decimalValue) {
        this.decimalValue = decimalValue;
    }

    public BigDecimal getDecimalValue() {
        return decimalValue;
    }


}