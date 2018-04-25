/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.parameter;

import com.audimex.dashboard.model.Parameter;
import com.audimex.dashboard.model.ParameterType;
import com.audimex.dashboard.model.param_value_types.*;
import com.haulmont.chile.core.datatypes.DatatypeRegistry;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;

import javax.inject.Inject;

import static com.audimex.dashboard.model.ParameterType.*;

public class ParameterEdit extends AbstractEditor<Parameter> {
    @Inject
    protected Datasource<Parameter> parameterDs;
    @Inject
    protected LookupField typeLookup;
    @Inject
    protected VBoxLayout valueBox;
    @Inject
    protected ComponentsFactory compFactory;
    @Inject
    protected DatatypeRegistry datatypes;

    protected TextField textField;
    protected DateField dateField;
    protected TimeField timeField;
    protected CheckBox checkBox;
    protected Label valueLabel;
    protected HBoxLayout hBox;

    @Override
    protected void postInit() {
        super.postInit();
        initValueFields();
        initValueTypeForScreen();
        typeLookup.addValueChangeListener(this::valueTypeChanged);
    }

    protected void initValueFields() {
        valueLabel = compFactory.createComponent(Label.class);
        valueLabel.setValue(getMessage("value"));
        valueLabel.setWidth("85px");
        checkBox = compFactory.createComponent(CheckBox.class);
        hBox = compFactory.createComponent(HBoxLayout.class);
        hBox.setSpacing(true);
        textField = compFactory.createComponent(TextField.class);
        dateField = compFactory.createComponent(DateField.class);
        timeField = compFactory.createComponent(TimeField.class);
    }

    protected void initValueTypeForScreen() {
        Value value = parameterDs.getItem().getValue();

        if (value instanceof EntityValue) {
            typeLookup.setValue(ENTITY);
        } else if (value instanceof ListEntitiesValue) {
            typeLookup.setValue(LIST_ENTITY);
        } else if (value instanceof EnumStringValue) {
            typeLookup.setValue(ENUM_STRING);
        } else if (value instanceof DateValue) {
            typeLookup.setValue(DATE);
        } else if (value instanceof DateTimeValue) {
            typeLookup.setValue(DATETIME);
        } else if (value instanceof TimeValue) {
            typeLookup.setValue(TIME);
        } else if (value instanceof UuidValue) {
            typeLookup.setValue(UUID);
        } else if (value instanceof IntegerValue) {
            typeLookup.setValue(INTEGER);
        } else if (value instanceof StringValue) {
            typeLookup.setValue(STRING);
        } else if (value instanceof DecimalValue) {
            typeLookup.setValue(DECIMAL);
        } else if (value instanceof BooleanValue) {
            typeLookup.setValue(BOOLEAN);
        } else { //if UNDEFINED
            typeLookup.setValue(UNDEFINED);
        }
    }

    protected void valueTypeChanged(ValueChangeEvent e) {
        ParameterType type = (ParameterType) e.getValue();

        switch (type) {
            case DATETIME:
                initDateField("DD/MM/yyyy hh:mm");
                break;
            case TIME:
                initTimeField();
                break;
            case DATE:
                initDateField("DD/MM/yyyy");
                break;
            case DECIMAL:
                initTextField("decimal");
                break;
            case INTEGER:
                initTextField("int");
                break;
            case LONG:
                initTextField("long");
                break;
            case STRING:
                initTextField(null);
                break;
            case BOOLEAN:
                initCheckBox();
                break;
            case UNDEFINED:
            default:
                valueBox.removeAll();
                break;
        }
    }

    protected void initDateField(String format) {
        dateField.setValue(null);
        dateField.setDateFormat(format);
        initSimpleField(dateField);
    }

    protected void initTimeField() {
        timeField.setValue(null);
        initSimpleField(timeField);
    }

    protected void initTextField(String dataType) {
        textField.setValue("");
        textField.setDatatype(dataType == null ? null : datatypes.get(dataType));
        initSimpleField(textField);
    }

    protected void initCheckBox() {
        checkBox.setValue(false);
        initSimpleField(checkBox);
    }

    protected void initSimpleField(Component field) {
        hBox.removeAll();
        hBox.add(valueLabel);
        hBox.add(field);
        valueBox.removeAll();
        valueBox.add(hBox);
    }

}