/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.parameter.frames;

import com.audimex.dashboard.model.ParameterType;
import com.audimex.dashboard.model.param_value_types.*;
import com.haulmont.chile.core.datatypes.DatatypeRegistry;
import com.haulmont.cuba.gui.components.*;

import javax.inject.Inject;
import java.util.Map;


public class SimpleValueFrame extends AbstractFrame implements ValueFrame {
    @Inject
    protected TextField textField;
    @Inject
    protected DateField dateField;
    @Inject
    protected TimeField timeField;
    @Inject
    protected CheckBox checkBox;
    @Inject
    protected HBoxLayout hBox;
    @Inject
    protected DatatypeRegistry datatypes;

    protected ParameterType type;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        type = (ParameterType) params.get(VALUE_TYPE);
        Value value = (Value) params.get(VALUE);
        showField(value);
    }

    @Override
    public Value getValue() {
        //todo add validation and validate exception
        switch (type) {
            case DATETIME:
                return new DateTimeValue(dateField.getValue());
            case TIME:
                return new TimeValue(timeField.getValue());
            case DATE:
                return new DateValue(dateField.getValue());
            case DECIMAL:
                return new DecimalValue(textField.getValue());
            case INTEGER:
                return new IntegerValue(textField.getValue());
            case LONG:
                return new LongValue(textField.getValue());
            case STRING:
                return new StringValue(textField.getValue());
            case BOOLEAN:
                return new BooleanValue(checkBox.getValue());
            default:
                return null;
        }
    }

    protected void showField(Value value) {
        switch (type) {
            case DATETIME:
                setDateTime((DateTimeValue) value);
                break;
            case TIME:
                setTime((TimeValue) value);
                break;
            case DATE:
                setDate((DateValue) value);
                break;
            case DECIMAL:
                setDecimal((DecimalValue) value);
                break;
            case INTEGER:
                setInteger((IntegerValue) value);
                break;
            case LONG:
                setLong((LongValue) value);
                break;
            case STRING:
                setString((StringValue) value);
                break;
            case BOOLEAN:
                setBoolean((BooleanValue) value);
                break;
        }
    }

    protected void setDateTime(DateTimeValue value) {
        dateField.setValue(value == null ? null : value.getValue());
        initDateField("DD/MM/yyyy hh:mm");
    }

    protected void setDate(DateValue value) {
        dateField.setValue(value == null ? null : value.getValue());
        initDateField("DD/MM/yyyy");
    }

    protected void initDateField(String format) {
        dateField.setDateFormat(format);
        dateField.setVisible(true);
    }

    protected void setTime(TimeValue value) {
        timeField.setValue(value == null ? null : value.getValue());
        timeField.setVisible(true);
    }

    protected void setDecimal(DecimalValue value) {
        textField.setValue(value == null ? null : value.getValue());
        initTextField("decimal");
    }

    protected void setInteger(IntegerValue value) {
        textField.setValue(value == null ? null : value.getValue());
        initTextField("int");
    }

    protected void setLong(LongValue value) {
        textField.setValue(value == null ? null : value.getValue());
        initTextField("long");
    }

    protected void setString(StringValue value) {
        textField.setValue(value == null ? null : value.getValue());
        initTextField(null);
    }

    protected void initTextField(String dataType) {
        textField.setDatatype(dataType == null ? null : datatypes.get(dataType));
        textField.setVisible(true);
    }

    protected void setBoolean(BooleanValue value) {
        checkBox.setValue(value == null ? null : value.getValue());
        checkBox.setVisible(true);
    }
}
