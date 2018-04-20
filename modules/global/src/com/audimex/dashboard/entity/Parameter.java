/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.entity;

import com.audimex.dashboard.entity.enums.ParameterInputType;
import com.audimex.dashboard.entity.parameter_types.*;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;

@NamePattern("%s|name")
@Table(name = "AMXD_PARAMETER")
@Entity(name = "amxd$Parameter")
public class Parameter extends StandardEntity {
    private static final long serialVersionUID = -6936139215635675937L;

    @Column(name = "NAME")
    protected String name;

    @Column(name = "ALIAS")
    protected String alias;

    @Column(name = "MAPPED_ALIAS")
    protected String mappedAlias;

    @Column(name = "ORDER_NUM")
    protected Integer orderNum;

    @Column(name = "INPUT_TYPE")
    protected Integer inputType;

    @Embedded
    protected EntityParameter entityParameter;
    @Embedded
    protected ListEntityParameter listEntityParameter;
    @AttributeOverrides({
        @AttributeOverride(name = "dateValue", column = @Column(name = "DATE_PARAMETER_DATE_VALUE"))
    })
    @Embedded
    protected DateParameter dateParameter;
    @AttributeOverrides({
        @AttributeOverride(name = "integerValue", column = @Column(name = "INTEGER_PARAMETER_INTEGER_VALUE"))
    })
    @Embedded
    protected IntegerParameter integerParameter;
    @Embedded
    protected StringParameter stringParameter;
    @AttributeOverrides({
        @AttributeOverride(name = "decimalValue", column = @Column(name = "DECIMAL_PARAMETER_DECIMAL_VALUE"))
    })
    @Embedded
    protected DecimalParameter decimalParameter;
    @Embedded
    protected BooleanParameter booleanParameter;
    @Embedded
    protected LongParameter longParameter;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DASHBOARD_ID")
    protected Dashboard dashboard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WIDGET_ID")
    protected Widget widget;

    public EntityParameter getEntityParameter() {
        return entityParameter;
    }

    public void setEntityParameter(EntityParameter entityParameter) {
        this.entityParameter = entityParameter;
    }

    public ListEntityParameter getListEntityParameter() {
        return listEntityParameter;
    }

    public void setListEntityParameter(ListEntityParameter listEntityParameter) {
        this.listEntityParameter = listEntityParameter;
    }

    public DateParameter getDateParameter() {
        return dateParameter;
    }

    public void setDateParameter(DateParameter dateParameter) {
        this.dateParameter = dateParameter;
    }

    public IntegerParameter getIntegerParameter() {
        return integerParameter;
    }

    public void setIntegerParameter(IntegerParameter integerParameter) {
        this.integerParameter = integerParameter;
    }

    public StringParameter getStringParameter() {
        return stringParameter;
    }

    public void setStringParameter(StringParameter stringParameter) {
        this.stringParameter = stringParameter;
    }

    public DecimalParameter getDecimalParameter() {
        return decimalParameter;
    }

    public void setDecimalParameter(DecimalParameter decimalParameter) {
        this.decimalParameter = decimalParameter;
    }

    public BooleanParameter getBooleanParameter() {
        return booleanParameter;
    }

    public void setBooleanParameter(BooleanParameter booleanParameter) {
        this.booleanParameter = booleanParameter;
    }

    public LongParameter getLongParameter() {
        return longParameter;
    }

    public void setLongParameter(LongParameter longParameter) {
        this.longParameter = longParameter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWidget(Widget widget) {
        this.widget = widget;
    }

    public Widget getWidget() {
        return widget;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setMappedAlias(String mappedAlias) {
        this.mappedAlias = mappedAlias;
    }

    public String getMappedAlias() {
        return mappedAlias;
    }

    public void setInputType(ParameterInputType inputType) {
        this.inputType = inputType == null ? null : inputType.getId();
    }

    public ParameterInputType getInputType() {
        return inputType == null ? null : ParameterInputType.fromId(inputType);
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }
}