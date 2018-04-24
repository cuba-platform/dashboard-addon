/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Lob;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "AMXD_PARAMETER_PERSIST")
@Entity(name = "amxd$ParameterPersist")
public class ParameterPersist extends StandardEntity {
    private static final long serialVersionUID = -5560885469527452724L;

    @Lob
    @Column(name = "PARAMETER_MODEL")
    protected String parameterModel;

    public void setParameterModel(String parameterModel) {
        this.parameterModel = parameterModel;
    }

    public String getParameterModel() {
        return parameterModel;
    }


}