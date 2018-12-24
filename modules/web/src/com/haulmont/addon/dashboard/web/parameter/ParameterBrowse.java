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

import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.Parameter;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.data.impl.AbstractDatasource;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame.DASHBOARD;
import static com.haulmont.cuba.gui.data.CollectionDatasource.Operation.REFRESH;

@UiController("dashboard$ParameterBrowse")
@UiDescriptor("parameter-browse.xml")
public class ParameterBrowse extends AbstractFrame {
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

    public GroupDatasource<Parameter, UUID> getParametersDs() {
        return parametersDs;
    }

    protected void initDs(Map<String, Object> params) {
        List<Parameter> parameters = (List<Parameter>) params.get(PARAMETERS);

        if (parameters == null) {
            parameters = new ArrayList<>();
        }

        for (Parameter param : parameters) {
            parametersDs.includeItem(param);
        }

        parametersDs.addCollectionChangeListener(event -> {
            if (REFRESH != event.getOperation()) {
                if (dashboard != null) {//if edit dashboard params
                    dashboard.setParameters(new ArrayList<>(event.getDs().getItems()));
                }
                ((AbstractDatasource) parametersDs).setModified(true);
            }
        });
        ((AbstractDatasource) parametersDs).setModified(false);
    }

    public Component generateValueCell(Entity entity) {
        String valueText = ((Parameter) entity).getParameterValue() == null ? StringUtils.EMPTY : ((Parameter) entity).getParameterValue().toString();
        return new Table.PlainTextCell(valueText);
    }
}
