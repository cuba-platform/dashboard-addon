/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.parameter;

import com.audimex.dashboard.model.Parameter;
import com.audimex.dashboard.model.ParameterType;
import com.audimex.dashboard.model.param_value_types.*;
import com.audimex.dashboard.web.parameter.frames.EntityValueFrame;
import com.audimex.dashboard.web.parameter.frames.EnumValueFrame;
import com.audimex.dashboard.web.parameter.frames.SimpleValueFrame;
import com.audimex.dashboard.web.parameter.frames.ValueFrame;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.gui.data.Datasource;

import javax.inject.Inject;

import static com.audimex.dashboard.model.ParameterType.*;
import static com.audimex.dashboard.web.parameter.frames.ValueFrame.*;

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
        typeLookup.addValueChangeListener(e -> parameterTypeChanged((ParameterType) e.getValue()));
    }

    @Override
    protected boolean preCommit() {
        Value value = valueFrame == null ? null : valueFrame.getValue();
        parameterDs.getItem().setValue(value);
        return super.preCommit();
    }

    protected void initParameter() {
        Value value = parameterDs.getItem().getValue();

        if (value instanceof EntityValue) {
            typeLookup.setValue(ENTITY);
            valueFrame = openEntityValueFrame((EntityValue) value);
        } else if (value instanceof ListEntitiesValue) {
            typeLookup.setValue(LIST_ENTITY);
        } else if (value instanceof EnumValue) {
            typeLookup.setValue(ENUM);
            valueFrame = openEnumValueFrame((EnumValue) value);
        } else if (value instanceof DateValue) {
            typeLookup.setValue(DATE);
            valueFrame = openSimpleValueFrame(DATE, value);
        } else if (value instanceof DateTimeValue) {
            typeLookup.setValue(DATETIME);
            valueFrame = openSimpleValueFrame(DATETIME, value);
        } else if (value instanceof TimeValue) {
            typeLookup.setValue(TIME);
            valueFrame = openSimpleValueFrame(TIME, value);
        } else if (value instanceof UuidValue) {
            typeLookup.setValue(UUID);
            valueFrame = openSimpleValueFrame(UUID, value);
        } else if (value instanceof IntegerValue) {
            typeLookup.setValue(INTEGER);
            valueFrame = openSimpleValueFrame(INTEGER, value);
        } else if (value instanceof StringValue) {
            typeLookup.setValue(STRING);
            valueFrame = openSimpleValueFrame(STRING, value);
        } else if (value instanceof DecimalValue) {
            typeLookup.setValue(DECIMAL);
            valueFrame = openSimpleValueFrame(DECIMAL, value);
        } else if (value instanceof BooleanValue) {
            typeLookup.setValue(BOOLEAN);
            valueFrame = openSimpleValueFrame(BOOLEAN, value);
        } else { //if UNDEFINED
            typeLookup.setValue(UNDEFINED);
        }
    }

    protected void parameterTypeChanged(ParameterType type) {
        switch (type) {
            case ENTITY:
                valueFrame = openEntityValueFrame(null);
                break;
            case ENUM:
                valueFrame = openEnumValueFrame(null);
                break;
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
                valueFrame = null;
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

    protected EnumValueFrame openEnumValueFrame(EnumValue value) {
        return (EnumValueFrame) openFrame(
                valueBox,
                "enumValueFrame",
                ParamsMap.of(VALUE, value)
        );
    }

    protected EntityValueFrame openEntityValueFrame(EntityValue value) {
        return (EntityValueFrame) openFrame(
                valueBox,
                "entityValueFrame",
                ParamsMap.of(VALUE, value)
        );
    }
}