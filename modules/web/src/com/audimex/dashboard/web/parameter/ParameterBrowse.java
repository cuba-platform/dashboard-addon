/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.parameter;

import com.audimex.dashboard.model.Dashboard;
import com.audimex.dashboard.model.Parameter;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.GroupDatasource;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.audimex.dashboard.web.dashboard.frames.editor.DashboardEdit.FROM_DASHBOARD_EDIT;
import static com.audimex.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame.DASHBOARD;

public class ParameterBrowse extends AbstractLookup {
    public static final String PARAMETERS = "PARAMETERS";
    public static final String SCREEN_NAME = "amdx$Parameter.browse";

    @Inject
    protected GroupDatasource<Parameter, UUID> parametersDs;
    @Inject
    protected Table<Parameter> parametersTable;

    protected Dashboard dashboard;

    protected Boolean fromDashboardEdit;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        dashboard = (Dashboard) params.get(DASHBOARD);
        fromDashboardEdit = (Boolean) params.get(FROM_DASHBOARD_EDIT);

        EditAction customEdit = new EditAction(parametersTable) {
            @Override
            protected void internalOpenEditor(CollectionDatasource datasource, Entity existingItem, Datasource parentDs, Map<String, Object> params) {
                super.internalOpenEditor(datasource, existingItem, datasource, ParamsMap.of(DASHBOARD, dashboard, FROM_DASHBOARD_EDIT, fromDashboardEdit));
            }
        };

        CreateAction customCreate = new CreateAction(parametersTable) {
            @Override
            protected void internalOpenEditor(CollectionDatasource datasource, Entity existingItem, Datasource parentDs, Map<String, Object> params) {
                super.internalOpenEditor(datasource, existingItem, datasource, ParamsMap.of(DASHBOARD, dashboard, FROM_DASHBOARD_EDIT, fromDashboardEdit));
            }

        };

        RemoveAction.BeforeActionPerformedHandler customRemoveBeforeActionPerformedHandler = () -> {
            Parameter parameter = parametersTable.getSingleSelected();
            if (fromDashboardEdit != null && fromDashboardEdit) {//open parameter frame from edit dashboard window
                dashboard.getParameters().remove(parameter);
            }
            return true;
        };

        RemoveAction customRemove = new RemoveAction(parametersTable);
        customRemove.setBeforeActionPerformedHandler(customRemoveBeforeActionPerformedHandler);

        parametersTable.addAction(customEdit);
        parametersTable.addAction(customCreate);
        parametersTable.addAction(customRemove);

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
