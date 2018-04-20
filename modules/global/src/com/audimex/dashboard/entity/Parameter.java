/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.entity;

import com.audimex.dashboard.entity.enums.ParameterInputType;
import com.audimex.dashboard.entity.parameter_types.*;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;

import com.audimex.dashboard.entity.enums.ParameterType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.audimex.dashboard.entity.enums.ParameterType.*;

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

    @Column(name = "PARAMETER_TYPE")
    protected Integer parameterType;

    @Embedded
    protected EntityParameter entityParameter;
    @Embedded
    protected ListEntityParameter listEntityParameter;
    @Embedded
    protected DateParameter dateParameter;
    @Embedded
    protected IntegerParameter integerParameter;
    @Embedded
    protected StringParameter stringParameter;
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

    public Object getParameter() {
        switch (getParameterType()) {
            case BOOLEAN:
                return booleanParameter.getBooleanValue();
            case DATE:
                return dateParameter.getDateValue();
            case DECIMAL:
                return decimalParameter.getDecimalValue();
            case ENTITY:
                return entityParameter;
            case INTEGER:
                return integerParameter.getIntegerValue();
            case LIST_ENTITY:
                return listEntityParameter;
            case LONG:
                return longParameter.getLongValue();
            case STRING:
                return stringParameter.getStringValue();
            case UNDEFINED:
            default:
                return null;
        }
    }

    public void setParameter(Object value) {
        if (value instanceof Boolean) {
            setParameterType(BOOLEAN);
            booleanParameter.setBooleanValue((Boolean) value);
        } else if (value instanceof Date) {
            setParameterType(DATE);
            dateParameter.setDateValue((Date) value);
        } else if (value instanceof BigDecimal) {
            setParameterType(DECIMAL);
            decimalParameter.setDecimalValue((BigDecimal) value);
        } else if (value instanceof EntityParameter) {
            setParameterType(ENTITY);
            //todo add parametr
        } else if (value instanceof Integer) {
            setParameterType(INTEGER);
            integerParameter.setIntegerValue((Integer) value);
        } else if (value instanceof ListEntityParameter) {
            setParameterType(LIST_ENTITY);
            //todo add parametr
        } else if (value instanceof Long) {
            setParameterType(LONG);
            longParameter.setLongValue((Long) value);
        } else if (value instanceof String) {
            setParameterType(STRING);
            stringParameter.setStringValue((String) value);
        } else {
            setParameterType(UNDEFINED);
        }
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

    public ParameterType getParameterType() {
        return parameterType == null ? ParameterType.UNDEFINED : ParameterType.fromId(parameterType);
    }

    protected void setParameterType(ParameterType type) {
        parameterType = type == null ? ParameterType.UNDEFINED.getId() : type.getId();
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }
}