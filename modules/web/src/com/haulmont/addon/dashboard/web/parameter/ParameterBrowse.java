/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.parameter;

import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.Parameter;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.GroupDatasource;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame.DASHBOARD;

public class ParameterBrowse extends AbstractLookup {
    public static final String PARAMETERS = "PARAMETERS";
    public static final String SCREEN_NAME = "dashboard$ParameterBrowse";

    @Inject
    protected GroupDatasource<Parameter, UUID> parametersDs;
    @Inject
    protected Table<Parameter> parametersTable;
    @WindowParam(name = DASHBOARD)
    protected Dashboard dashboard;

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

        parametersDs.addCollectionChangeListener(event -> {
            if (dashboard != null) {//if edit dashboard params
                dashboard.setParameters(new ArrayList<>(event.getDs().getItems()));
            }
        });
    }

    public Component generateValueCell(Entity entity) {
        String valueText = ((Parameter) entity).getParameterValue() == null ? StringUtils.EMPTY : ((Parameter) entity).getParameterValue().toString();
        return new Table.PlainTextCell(valueText);
    }
}
