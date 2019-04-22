/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.addon.dashboard.web.parameter.frames;

import com.haulmont.addon.dashboard.model.ParameterType;
import com.haulmont.addon.dashboard.model.paramtypes.*;
import com.haulmont.chile.core.datatypes.DatatypeRegistry;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@UiController("dashboard$SimpleValueFrame")
@UiDescriptor("simple-value-frame.xml")
public class SimpleValueFrame extends AbstractFrame implements ValueFrame {
    @Inject
    protected TextField<Integer> intField;
    @Inject
    protected TextField<BigDecimal> decimalField;
    @Inject
    protected TextField<Long> longField;
    @Inject
    protected TextField<UUID> uuidField;
    @Inject
    protected TextField<String> textField;
    @Inject
    protected DateField<Date> dateField;
    @Inject
    protected TimeField<Date> timeField;
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
        ParameterValue parameterValue = (ParameterValue) params.get(VALUE);
        showField(parameterValue);
    }

    @Override
    public ParameterValue getValue() {
        switch (type) {
            case DATETIME:
                return new DateTimeParameterValue(dateField.getValue());
            case TIME:
                return new TimeParameterValue(timeField.getValue());
            case DATE:
                return new DateParameterValue(dateField.getValue());
            case DECIMAL:
                return new DecimalParameterValue(decimalField.getValue());
            case INTEGER:
                return new IntegerParameterValue(intField.getValue());
            case LONG:
                return new LongParameterValue(longField.getValue());
            case STRING:
                return new StringParameterValue(textField.getValue());
            case UUID:
                return new UuidParameterValue(uuidField.getValue());
            case BOOLEAN:
                return new BooleanParameterValue(checkBox.getValue());
            default:
                return null;
        }
    }

    protected void showField(ParameterValue parameterValue) {
        switch (type) {
            case DATETIME:
                setDateTime((DateTimeParameterValue) parameterValue);
                break;
            case TIME:
                setTime((TimeParameterValue) parameterValue);
                break;
            case DATE:
                setDate((DateParameterValue) parameterValue);
                break;
            case DECIMAL:
                setDecimal((DecimalParameterValue) parameterValue);
                break;
            case INTEGER:
                setInteger((IntegerParameterValue) parameterValue);
                break;
            case LONG:
                setLong((LongParameterValue) parameterValue);
                break;
            case STRING:
                setString((StringParameterValue) parameterValue);
                break;
            case UUID:
                setUUID((UuidParameterValue) parameterValue);
                break;
            case BOOLEAN:
                setBoolean((BooleanParameterValue) parameterValue);
                break;
        }
    }

    protected void setDateTime(DateTimeParameterValue value) {
        dateField.setValue(value == null ? null : value.getValue());
        initDateField("dd/MM/yyyy HH:mm");
    }

    protected void setDate(DateParameterValue value) {
        dateField.setValue(value == null ? null : value.getValue());
        initDateField("dd/MM/yyyy");
    }

    protected void initDateField(String format) {
        dateField.setDateFormat(format);
        dateField.setVisible(true);
    }

    protected void setTime(TimeParameterValue value) {
        timeField.setValue(value == null ? null : value.getValue());
        timeField.setVisible(true);
    }

    protected void setDecimal(DecimalParameterValue value) {
        decimalField.setValue(value == null ? null : value.getValue());
        decimalField.setVisible(true);
    }

    protected void setInteger(IntegerParameterValue value) {
        intField.setValue(value == null ? null : value.getValue());
        intField.setVisible(true);
    }

    protected void setLong(LongParameterValue value) {
        longField.setValue(value == null ? null : value.getValue());
        longField.setVisible(true);
    }

    protected void setString(StringParameterValue value) {
        textField.setValue(value == null ? null : value.getValue());
        textField.setVisible(true);
    }

    protected void setUUID(UuidParameterValue value) {
        uuidField.setValue(value == null ? null : value.getValue());
        uuidField.setVisible(true);
    }

    protected void initTextField(String dataType) {
        textField.setDatatype(dataType == null ? null : datatypes.get(dataType));
        textField.setVisible(true);
    }

    protected void setBoolean(BooleanParameterValue value) {
        checkBox.setValue(value == null ? null : value.getValue());
        checkBox.setVisible(true);
    }
}
