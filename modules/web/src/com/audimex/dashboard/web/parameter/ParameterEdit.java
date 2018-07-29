/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.parameter;

import com.audimex.dashboard.model.Dashboard;
import com.audimex.dashboard.model.Parameter;
import com.audimex.dashboard.model.ParameterType;
import com.audimex.dashboard.model.param_value_types.*;
import com.audimex.dashboard.web.parameter.frames.*;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.gui.data.Datasource;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.audimex.dashboard.model.ParameterType.*;
import static com.audimex.dashboard.web.dashboard.frames.editor.DashboardEdit.FROM_DASHBOARD_EDIT;
import static com.audimex.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame.DASHBOARD;
import static com.audimex.dashboard.web.parameter.frames.ValueFrame.VALUE;
import static com.audimex.dashboard.web.parameter.frames.ValueFrame.VALUE_TYPE;

public class ParameterEdit extends AbstractEditor<Parameter> {
    @Inject
    protected Datasource<Parameter> parameterDs;
    @Inject
    protected LookupField typeLookup;
    @Inject
    protected VBoxLayout valueBox;

    //The AbstractEditor replaces an item to another object, if one has status '[new]'
    @WindowParam(name = "ITEM", required = true)
    protected Parameter inputItem;

    protected ValueFrame valueFrame;

    protected Dashboard dashboard;

    protected Boolean fromDashboardEdit;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        dashboard = (Dashboard) params.get(DASHBOARD);
        fromDashboardEdit = (Boolean) params.get(FROM_DASHBOARD_EDIT);
        typeLookup.setOptionsList(Arrays.asList(ParameterType.values()));
        if (fromDashboardEdit != null && fromDashboardEdit) {//open parameter frame from edit dashboard window
            List tmpLst = new ArrayList(typeLookup.getOptionsList());
            tmpLst.remove(DASHBOARD_PARAMETER);
            typeLookup.setOptionsList(tmpLst);
        }
    }

    @Override
    protected void postInit() {
        super.postInit();
        parameterDs.setItem(inputItem);
        initParameter();
        typeLookup.addValueChangeListener(e -> parameterTypeChanged((ParameterType) e.getValue()));
    }

    @Override
    protected boolean preCommit() {
        ParameterValue parameterValue = valueFrame == null ? null : valueFrame.getValue();
        parameterDs.getItem().setParameterValue(parameterValue);
        return super.preCommit();
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (fromDashboardEdit != null && fromDashboardEdit) {//open parameter frame from edit dashboard window
            dashboard.getParameters().remove(parameterDs.getItem());
            dashboard.getParameters().add(parameterDs.getItem());
        }
        return super.postCommit(committed, close);
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
        } else if (parameterValue instanceof DashboardParameterParameterValue) {
            typeLookup.setValue(DASHBOARD_PARAMETER);
            valueFrame = openDashboardParameterFrame((DashboardParameterParameterValue) parameterValue);
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
            case DASHBOARD_PARAMETER:
                valueFrame = openDashboardParameterFrame(null);
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
                "simpleValueFrame",
                ParamsMap.of(VALUE_TYPE, type, VALUE, parameterValue, DASHBOARD, dashboard)
        );
    }

    protected EnumValueFrame openEnumValueFrame(EnumParameterValue value) {
        return (EnumValueFrame) openFrame(
                valueBox,
                "enumValueFrame",
                ParamsMap.of(VALUE, value, DASHBOARD, dashboard)
        );
    }

    protected EntityValueFrame openEntityValueFrame(EntityParameterValue value) {
        return (EntityValueFrame) openFrame(
                valueBox,
                "entityValueFrame",
                ParamsMap.of(VALUE, value, DASHBOARD, dashboard)
        );
    }

    protected EntitiesListValueFrame openEntitiesListValueFrame(ListEntitiesParameterValue value) {
        return (EntitiesListValueFrame) openFrame(
                valueBox,
                "entitiesListValueFrame",
                ParamsMap.of(VALUE, value, DASHBOARD, dashboard)
        );
    }

    protected DashboardParameterValueFrame openDashboardParameterFrame(DashboardParameterParameterValue value) {
        return (DashboardParameterValueFrame) openFrame(
                valueBox,
                "dashboardParameterValueFrame",
                ParamsMap.of(VALUE, value, DASHBOARD, dashboard)
        );
    }
}