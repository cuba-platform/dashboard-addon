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
package com.haulmont.addon.dashboard.web.widget;

import com.haulmont.addon.dashboard.model.Parameter;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.web.dashboard.tools.AccessConstraintsHelper;
import com.haulmont.addon.dashboard.web.parameter.ParameterBrowse;
import com.haulmont.addon.dashboard.web.repository.WidgetRepository;
import com.haulmont.addon.dashboard.web.repository.WidgetTypeInfo;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.AbstractDatasource;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

import static com.haulmont.addon.dashboard.web.parameter.ParameterBrowse.PARAMETERS;

public class WidgetEdit extends AbstractEditor<Widget> {
    public static final String SCREEN_NAME = "dashboard$Widget.edit";
    public static final String ITEM_DS = "ITEM_DS";

    @Inject
    protected Datasource<Widget> widgetDs;
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected VBoxLayout widgetEditBox;
    @Inject
    protected ParameterBrowse paramsFrame;
    @Inject
    protected WidgetRepository widgetRepository;
    @Inject
    protected AccessConstraintsHelper accessHelper;

    @Named("fieldGroup.caption")
    protected TextField widgetCaption;

    @Named("fieldGroup.widgetId")
    protected TextField widgetId;


    //The AbstractEditor replaces an item to another object, if one has status '[new]'
    @WindowParam(name = "ITEM", required = true)
    protected Widget inputItem;

    protected List<WidgetTypeInfo> typesInfo;
    protected AbstractFrame widgetEditFrame;

    @Override
    public void postInit() {
        widgetDs.setItem(inputItem);
        typesInfo = widgetRepository.getWidgetTypesInfo();
        setWidgetType();
        initParametersFrame();
        widgetCaption.addValueChangeListener(v -> {
            if (StringUtils.isEmpty(widgetId.getValue())) {
                widgetId.setValue(v.getValue());
            }
        });

        ((AbstractDatasource) widgetDs).setModified(false);
    }

    @Override
    public Widget getItem() {
        Widget item = super.getItem();
        item.setCreatedBy(accessHelper.getCurrentSessionLogin());
        return item;
    }

    protected void setWidgetType() {
        String browseFrameId = widgetDs.getItem().getFrameId();

        WidgetTypeInfo widgetType = typesInfo.stream()
                .filter(typeInfo -> browseFrameId.equals(typeInfo.getFrameId()))
                .findFirst().orElseThrow(() -> new RuntimeException("Unknown widget type"));


        Map<String, Object> params = new HashMap<>(ParamsMap.of(ITEM_DS, widgetDs));
        params.putAll(widgetRepository.getWidgetParams(widgetDs.getItem()));
        if (StringUtils.isNotEmpty(widgetType.getEditFrameId())) {
            widgetEditFrame = openFrame(widgetEditBox, widgetType.getEditFrameId(), params);
        }
    }

    protected void initParametersFrame() {
        paramsFrame.init(ParamsMap.of(
                PARAMETERS,
                getItem().getParameters()
        ));
    }


    @Override
    protected boolean preCommit() {
        List<Parameter> parameters = paramsFrame.getParameters();
        getItem().setParameters(parameters);
        if (widgetEditFrame != null) {
            widgetRepository.serializeWidgetFields(widgetEditFrame, inputItem);
        }
        return super.preCommit();
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        if (inputItem.getDashboard() != null) {
            List<Widget> dashboardWidgets = inputItem.getDashboard().getWidgets();
            long cnt = dashboardWidgets.stream()
                    .filter(w -> !w.getId().equals(inputItem.getId()) && w.getWidgetId().equals(inputItem.getWidgetId()))
                    .count();
            if (cnt > 0) {
                errors.add(fieldGroup.getComponent("widgetId"), getMessage("uniqueWidgetId"));
            }
        }
    }

    @Override
    protected boolean preClose(String actionId) {
        if (!widgetDs.isModified()) {
            if (paramsFrame.getParametersDs().isModified()) {
                ((AbstractDatasource) widgetDs).setModified(true);
            }
        }
        return super.preClose(actionId);

    }
}