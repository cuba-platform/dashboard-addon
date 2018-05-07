/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.model;

import com.audimex.dashboard.model.param_value_types.ParameterValue;
import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|name")
@MetaClass(name = "amxd$Parameter")
public class Parameter extends BaseUuidEntity {
    @MetaProperty
    protected String name;
    @MetaProperty
    protected String alias;
    @MetaProperty
    protected String mappedAlias;
    @MetaProperty
    protected Integer orderNum;

    protected ParameterValue parameterValue;

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public void setParameterValue(ParameterValue parameterValue) {
        this.parameterValue = parameterValue;
    }

    public ParameterValue getParameterValue() {
        return parameterValue;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setMappedAlias(String mappedAlias) {
        this.mappedAlias = mappedAlias;
    }

    public String getMappedAlias() {
        return mappedAlias;
    }
}