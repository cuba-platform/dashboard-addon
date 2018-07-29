/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.parameter;

import com.haulmont.addon.dashboard.model.Parameter;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.GroupDatasource;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ParameterBrowse extends AbstractLookup {
    public static final String PARAMETERS = "PARAMETERS";
    public static final String SCREEN_NAME = "amdx$Parameter.browse";

    @Inject
    protected GroupDatasource<Parameter, UUID> parametersDs;
    @Inject
    protected Table<Parameter> parametersTable;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initDs(params);
    }

    public List<Parameter> getParameters() {
        return new ArrayList<>(parametersDs.getItems());
    }

    protected void initDs(Map<String, Object> params) {
        List<Parameter> parameters = (List<Parameter>) params.get(PARAMETERS);

        if (parameters == null) {
            parameters = new ArrayList<>();
        }

        for (Parameter param : parameters) {
            parametersDs.addItem(param);
        }
    }

    public Component generateValueCell(Entity entity) {
        String valueText = ((Parameter) entity).getParameterValue().toString();
        return new Table.PlainTextCell(valueText);
    }
}
