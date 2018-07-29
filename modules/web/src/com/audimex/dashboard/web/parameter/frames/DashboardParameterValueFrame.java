/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.parameter.frames;

import com.audimex.dashboard.model.Dashboard;
import com.audimex.dashboard.model.Parameter;
import com.audimex.dashboard.model.ParameterType;
import com.audimex.dashboard.model.param_value_types.*;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.VBoxLayout;

import javax.inject.Inject;
import java.util.Map;

import static com.audimex.dashboard.model.ParameterType.*;
import static com.audimex.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame.DASHBOARD;


public class DashboardParameterValueFrame extends AbstractFrame implements ValueFrame {

    @Inject
    protected LookupField dashboardParameterLookup;

    @Inject
    protected Metadata metadata;

    @Inject
    protected VBoxLayout valueBox;

    protected ValueFrame valueFrame;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        Dashboard dashboard = (Dashboard) params.get(DASHBOARD);
        loadDashboardParameters(dashboard);
        selectIfExist(dashboard, (DashboardParameterParameterValue) params.get("VALUE"));
        dashboardParameterLookup.addValueChangeListener(e -> dashboardParameterValueChanged((Parameter) e.getValue()));
    }

    @Override
    public ParameterValue getValue() {
        Parameter value = dashboardParameterLookup.getValue();
        return value == null ? null : new DashboardParameterParameterValue(value);
    }

    protected void loadDashboardParameters(Dashboard dashboard) {
        dashboardParameterLookup.setOptionsList(dashboard.getParameters());
    }

    public void selectIfExist(Dashboard dashboard, DashboardParameterParameterValue value) {
        if (value != null && value.getValue() != null) {
            dashboard.getParameters().stream().filter(par -> value.getValue().equals(par)).findFirst().ifPresent(par -> {
                dashboardParameterLookup.setValue(par);
                dashboardParameterValueChanged(par);
            });
        }
    }


    protected void dashboardParameterValueChanged(Parameter parameter) {
        ParameterValue parameterValue = parameter.getParameterValue();
        if (parameterValue instanceof EntityParameterValue) {
            valueFrame = openEntityValueFrame((EntityParameterValue) parameterValue);
        } else if (parameterValue instanceof ListEntitiesParameterValue) {
            valueFrame = openEntitiesListValueFrame((ListEntitiesParameterValue) parameterValue);
        } else if (parameterValue instanceof EnumParameterValue) {
            valueFrame = openEnumValueFrame((EnumParameterValue) parameterValue);
        } else if (parameterValue instanceof DateParameterValue) {
            valueFrame = openSimpleValueFrame(DATE, parameterValue);
        } else if (parameterValue instanceof DateTimeParameterValue) {
            valueFrame = openSimpleValueFrame(DATETIME, parameterValue);
        } else if (parameterValue instanceof TimeParameterValue) {
            valueFrame = openSimpleValueFrame(TIME, parameterValue);
        } else if (parameterValue instanceof UuidParameterValue) {
            valueFrame = openSimpleValueFrame(UUID, parameterValue);
        } else if (parameterValue instanceof IntegerParameterValue) {
            valueFrame = openSimpleValueFrame(INTEGER, parameterValue);
        } else if (parameterValue instanceof StringParameterValue) {
            valueFrame = openSimpleValueFrame(STRING, parameterValue);
        } else if (parameterValue instanceof DecimalParameterValue) {
            valueFrame = openSimpleValueFrame(DECIMAL, parameterValue);
        } else if (parameterValue instanceof BooleanParameterValue) {
            valueFrame = openSimpleValueFrame(BOOLEAN, parameterValue);
        } else if (parameterValue instanceof DashboardParameterParameterValue) {
            valueFrame = openDashboardParameterFrame((DashboardParameterParameterValue) parameterValue);
        }
        valueBox.setEnabled(false);
    }

    protected SimpleValueFrame openSimpleValueFrame(ParameterType type, ParameterValue parameterValue) {
        return (SimpleValueFrame) openFrame(
                valueBox,
                "simpleValueFrame",
                ParamsMap.of(VALUE_TYPE, type, VALUE, parameterValue)
        );
    }

    protected EnumValueFrame openEnumValueFrame(EnumParameterValue value) {
        return (EnumValueFrame) openFrame(
                valueBox,
                "enumValueFrame",
                ParamsMap.of(VALUE, value)
        );
    }

    protected EntityValueFrame openEntityValueFrame(EntityParameterValue value) {
        return (EntityValueFrame) openFrame(
                valueBox,
                "entityValueFrame",
                ParamsMap.of(VALUE, value)
        );
    }

    protected EntitiesListValueFrame openEntitiesListValueFrame(ListEntitiesParameterValue value) {
        return (EntitiesListValueFrame) openFrame(
                valueBox,
                "entitiesListValueFrame",
                ParamsMap.of(VALUE, value)
        );
    }

    protected EntitiesListValueFrame openDashboardParameterFrame(DashboardParameterParameterValue value) {
        return (EntitiesListValueFrame) openFrame(
                valueBox,
                "dashboardParameterValueFrame",
                ParamsMap.of(VALUE, value)
        );
    }

}