/*
 * Copyright (c) 2008-2018 Haulmont.
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
 *
 */
package com.haulmont.addon.dashboard.web.parameter;

import com.haulmont.addon.dashboard.model.Parameter;
import com.haulmont.addon.dashboard.model.ParameterType;
import com.haulmont.addon.dashboard.model.paramtypes.*;
import com.haulmont.addon.dashboard.web.parameter.frames.*;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.gui.data.Datasource;

import javax.inject.Inject;
import java.util.Map;

import static com.haulmont.addon.dashboard.model.ParameterType.*;

public class ParameterEdit extends AbstractEditor<Parameter> {
    @Inject
    protected Datasource<Parameter> parameterDs;
    @Inject
    protected LookupField typeLookup;
    @Inject
    protected VBoxLayout valueBox;

    protected ValueFrame valueFrame;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected void postInit() {
        super.postInit();
        initParameter();
        typeLookup.addValueChangeListener(e -> parameterTypeChanged((ParameterType) e.getValue()));
    }

    @Override
    protected boolean preCommit() {
        ParameterValue parameterValue = valueFrame == null ? null : valueFrame.getValue();
        parameterDs.getItem().setParameterValue(parameterValue);
        return super.preCommit();
    }

    protected void initParameter() {
        ParameterValue parameterValue = parameterDs.getItem().getParameterValue();

        if (parameterValue instanceof EntityParameterValue) {
            typeLookup.setValue(ENTITY);
            valueFrame = openEntityValueFrame((EntityParameterValue) parameterValue);
        } else if (parameterValue instanceof ListEntitiesParameterValue) {
            typeLookup.setValue(LIST_ENTITY);
            valueFrame = openEntitiesListValueFrame((ListEntitiesParameterValue) parameterValue);
        } else if (parameterValue instanceof EnumParameterValue) {
            typeLookup.setValue(ENUM);
            valueFrame = openEnumValueFrame((EnumParameterValue) parameterValue);
        } else if (parameterValue instanceof DateParameterValue) {
            typeLookup.setValue(DATE);
            valueFrame = openSimpleValueFrame(DATE, parameterValue);
        } else if (parameterValue instanceof DateTimeParameterValue) {
            typeLookup.setValue(DATETIME);
            valueFrame = openSimpleValueFrame(DATETIME, parameterValue);
        } else if (parameterValue instanceof TimeParameterValue) {
            typeLookup.setValue(TIME);
            valueFrame = openSimpleValueFrame(TIME, parameterValue);
        } else if (parameterValue instanceof UuidParameterValue) {
            typeLookup.setValue(UUID);
            valueFrame = openSimpleValueFrame(UUID, parameterValue);
        } else if (parameterValue instanceof IntegerParameterValue) {
            typeLookup.setValue(INTEGER);
            valueFrame = openSimpleValueFrame(INTEGER, parameterValue);
        } else if (parameterValue instanceof StringParameterValue) {
            typeLookup.setValue(STRING);
            valueFrame = openSimpleValueFrame(STRING, parameterValue);
        } else if (parameterValue instanceof DecimalParameterValue) {
            typeLookup.setValue(DECIMAL);
            valueFrame = openSimpleValueFrame(DECIMAL, parameterValue);
        } else if (parameterValue instanceof BooleanParameterValue) {
            typeLookup.setValue(BOOLEAN);
            valueFrame = openSimpleValueFrame(BOOLEAN, parameterValue);
        } else { //if UNDEFINED
            typeLookup.setValue(UNDEFINED);
        }
    }

    protected void parameterTypeChanged(ParameterType type) {
        switch (type) {
            case LIST_ENTITY:
                valueFrame = openEntitiesListValueFrame(new ListEntitiesParameterValue());
                break;
            case ENTITY:
                valueFrame = openEntityValueFrame(new EntityParameterValue());
                break;
            case ENUM:
                valueFrame = openEnumValueFrame(new EnumParameterValue());
                break;
            case DATETIME:
            case TIME:
            case DATE:
            case DECIMAL:
            case INTEGER:
            case LONG:
            case STRING:
            case BOOLEAN:
            case UUID:
                valueFrame = openSimpleValueFrame(type, null);
                break;
            case UNDEFINED:
            default:
                valueFrame = null;
                valueBox.removeAll();
                break;
        }
    }

    protected SimpleValueFrame openSimpleValueFrame(ParameterType type, ParameterValue parameterValue) {
        return (SimpleValueFrame) openFrame(
                valueBox,
                "dashboard$SimpleValueFrame",
                ParamsMap.of(ValueFrame.VALUE_TYPE, type, ValueFrame.VALUE, parameterValue)
        );
    }

    protected EnumValueFrame openEnumValueFrame(EnumParameterValue value) {
        return (EnumValueFrame) openFrame(
                valueBox,
                "dashboard$EnumValueFrame",
                ParamsMap.of(ValueFrame.VALUE, value)
        );
    }

    protected EntityValueFrame openEntityValueFrame(EntityParameterValue value) {
        return (EntityValueFrame) openFrame(
                valueBox,
                "dashboard$EntityValueFrame",
                ParamsMap.of(ValueFrame.VALUE, value)
        );
    }

    protected EntitiesListValueFrame openEntitiesListValueFrame(ListEntitiesParameterValue value) {
        return (EntitiesListValueFrame) openFrame(
                valueBox,
                "dashboard$EntitiesListValueFrame",
                ParamsMap.of(ValueFrame.VALUE, value)
        );
    }
}