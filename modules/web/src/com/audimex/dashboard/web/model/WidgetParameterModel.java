/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author bochkarev
 * @version $Id$
 */
public class WidgetParameterModel implements Serializable {

    private static final long serialVersionUID = 5192647316326329176L;

    protected String name;
    protected String alias;
    protected String mappedAlias;
    protected Integer inputType;
    protected Integer parameterType;
    protected Integer integerValue;
    protected String stringValue;
    protected BigDecimal decimalValue;
    protected Date dateValue;
    protected Boolean boolValue;
    protected Long longValue;
    protected UUID entityId;
    protected String metaClassName;
    protected String viewName;
    protected Double doubleValue;
    protected UUID masterParameter;
    protected List<WidgetParameterModel> listParameters;
    protected List<WidgetParameterModel> additionalParameters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParameterType() {
        return parameterType;
    }

    public void setParameterType(Integer parameterType) {
        this.parameterType = parameterType;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public BigDecimal getDecimalValue() {
        return decimalValue;
    }

    public void setDecimalValue(BigDecimal decimalValue) {
        this.decimalValue = decimalValue;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    public Boolean getBoolValue() {
        return boolValue;
    }

    public void setBoolValue(Boolean boolValue) {
        this.boolValue = boolValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

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

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public UUID getMasterParameter() {
        return masterParameter;
    }

    public void setMasterParameter(UUID masterParameter) {
        this.masterParameter = masterParameter;
    }

    public List<WidgetParameterModel> getListParameters() {
        return listParameters;
    }

    public void setListParameters(List<WidgetParameterModel> listParameters) {
        this.listParameters = listParameters;
    }

    public List<WidgetParameterModel> getAdditionalParameters() {
        return additionalParameters;
    }

    public void setAdditionalParameters(List<WidgetParameterModel> additionalParameters) {
        this.additionalParameters = additionalParameters;
    }

    public void addAdditionalParameter(WidgetParameterModel additionalParameter) {
        if (additionalParameters == null) {
            additionalParameters = new ArrayList<>();
        }

        additionalParameters.add(additionalParameter);
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Integer getInputType() {
        return inputType;
    }

    public void setInputType(Integer inputType) {
        this.inputType = inputType;
    }

    public String getMappedAlias() {
        return mappedAlias;
    }

    public void setMappedAlias(String mappedAlias) {
        this.mappedAlias = mappedAlias;
    }
}
