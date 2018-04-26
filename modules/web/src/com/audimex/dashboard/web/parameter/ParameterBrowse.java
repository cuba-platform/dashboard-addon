/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.parameter;

import com.audimex.dashboard.model.HasParameters;
import com.audimex.dashboard.model.Parameter;
import com.audimex.dashboard.model.param_value_types.StringValue;
import com.audimex.dashboard.model.param_value_types.Value;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.GroupDatasource;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

import static com.haulmont.cuba.gui.components.Frame.NotificationType.ERROR;

public class ParameterBrowse extends AbstractLookup {
    public static final String OBJECT_WITH_PARAMETERS = "OBJECT_WITH_PARAMETERS";

    @Inject
    protected GroupDatasource<Parameter, UUID> parametersDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        Value value = new StringValue("strValue");

        Parameter p = new Parameter();
        p.setName("newName");
        p.setAlias("newAlias");
        p.setMappedAlias("newMappedAlias");
        p.setOrderNum(1);
        p.setValue(value);

        parametersDs.addItem(p);

//        initDs(params);
        //todo add work with orderNum
        //todo add filter
    }

    protected void initDs(Map<String, Object> params) {
        HasParameters object = (HasParameters) params.get(OBJECT_WITH_PARAMETERS);

        if(object == null || object.getParameters() == null){
            showNotification(getMessage("noObject"), ERROR);
            return;
        }

        for (Parameter param : object.getParameters()) {
           parametersDs.addItem(param);
        }
    }
}
