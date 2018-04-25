/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.parameter;

import com.audimex.dashboard.model.Parameter;
import com.audimex.dashboard.model.ParameterType;
import com.audimex.dashboard.model.param_value_types.*;
import com.audimex.dashboard.web.parameter.frames.SimpleValueFrame;
import com.audimex.dashboard.web.parameter.frames.ValueFrame;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;

import javax.inject.Inject;

import static com.audimex.dashboard.model.ParameterType.*;
import static com.audimex.dashboard.web.parameter.frames.SimpleValueFrame.VALUE;
import static com.audimex.dashboard.web.parameter.frames.SimpleValueFrame.VALUE_TYPE;

public class ParameterEdit extends AbstractEditor<Parameter> {
    @Inject
    protected Datasource<Parameter> parameterDs;
    @Inject
    protected LookupField typeLookup;
    @Inject
    protected VBoxLayout valueBox;

    protected ValueFrame valueFrame;

    @Override
    protected void postInit() {
        super.postInit();
        initParameter();
        typeLookup.addValueChangeListener(this::parameterTypeChanged);
    }

    protected void initParameter() {
        Value value = parameterDs.getItem().getValue();

        if (value instanceof EntityValue) {
            typeLookup.setValue(ENTITY);
        } else if (value instanceof ListEntitiesValue) {
            typeLookup.setValue(LIST_ENTITY);
        } else if (value instanceof EnumStringValue) {
            typeLookup.setValue(ENUM_STRING);
        } else if (value instanceof DateValue) {
            typeLookup.setValue(DATE);
            openSimpleValueFrame(DATE, value);
        } else if (value instanceof DateTimeValue) {
            typeLookup.setValue(DATETIME);
            openSimpleValueFrame(DATETIME, value);
        } else if (value instanceof TimeValue) {
            typeLookup.setValue(TIME);
            openSimpleValueFrame(TIME, value);
        } else if (value instanceof UuidValue) {
            typeLookup.setValue(UUID);
            openSimpleValueFrame(UUID, value);
        } else if (value instanceof IntegerValue) {
            typeLookup.setValue(INTEGER);
            openSimpleValueFrame(INTEGER, value);
        } else if (value instanceof StringValue) {
            typeLookup.setValue(STRING);
            openSimpleValueFrame(STRING, value);
        } else if (value instanceof DecimalValue) {
            typeLookup.setValue(DECIMAL);
            openSimpleValueFrame(DECIMAL, value);
        } else if (value instanceof BooleanValue) {
            typeLookup.setValue(BOOLEAN);
            openSimpleValueFrame(BOOLEAN, value);
        } else { //if UNDEFINED
            typeLookup.setValue(UNDEFINED);
        }
    }

    protected void parameterTypeChanged(ValueChangeEvent e) {
        ParameterType type = (ParameterType) e.getValue();

        switch (type) {
            case DATETIME:
            case TIME:
            case DATE:
            case DECIMAL:
            case INTEGER:
            case LONG:
            case STRING:
            case BOOLEAN:
                valueFrame = openSimpleValueFrame(type, null);
                break;
            case UNDEFINED:
            default:
                valueBox.removeAll();
                break;
        }
    }

    protected SimpleValueFrame openSimpleValueFrame(ParameterType type, Value value) {
        return (SimpleValueFrame) openFrame(
                valueBox,
                "simpleValueFrame",
                ParamsMap.of(VALUE_TYPE, type, VALUE, value)
        );
    }

}