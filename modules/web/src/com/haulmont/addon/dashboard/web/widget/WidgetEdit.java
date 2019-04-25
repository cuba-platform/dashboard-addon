/*
 * Copyright (c) 2008-2019 Haulmont.
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
 */
package com.haulmont.addon.dashboard.web.widget;

import com.haulmont.addon.dashboard.model.Parameter;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.web.dashboard.tools.AccessConstraintsHelper;
import com.haulmont.addon.dashboard.web.parameter.ParameterBrowse;
import com.haulmont.addon.dashboard.web.repository.WidgetRepository;
import com.haulmont.addon.dashboard.web.repository.WidgetTypeInfo;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.Fragments;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.AbstractDatasource;
import com.haulmont.cuba.gui.screen.MapScreenOptions;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.haulmont.addon.dashboard.web.parameter.ParameterBrowse.PARAMETERS;

@UiController("dashboard$Widget.edit")
@UiDescriptor("widget-edit.xml")
public class WidgetEdit extends AbstractEditor<Widget> {
    public static final String SCREEN_NAME = "dashboard$Widget.edit";
    public static final String ITEM_DS = "ITEM_DS";

    @Inject
    protected Datasource<Widget> widgetDs;
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected OrderedContainer widgetEditBox;
    @Inject
    protected ParameterBrowse paramsFrame;
    @Inject
    protected WidgetRepository widgetRepository;
    @Inject
    protected AccessConstraintsHelper accessHelper;
    @Inject
    protected Fragments fragments;

    @Named("fieldGroup.caption")
    protected TextField<String> widgetCaption;

    @Named("fieldGroup.widgetId")
    protected TextField<String> widgetId;

    protected List<WidgetTypeInfo> typesInfo;
    protected AbstractFrame widgetEditFrame;

    @Override
    public void postInit() {
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
            widgetEditFrame = (AbstractFrame) fragments.create(this,
                    widgetType.getEditFrameId(),
                    new MapScreenOptions(params))
                    .init();
            widgetEditBox.removeAll();
            widgetEditBox.add(widgetEditFrame.getFragment());
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
            widgetRepository.serializeWidgetFields(widgetEditFrame, widgetDs.getItem());
        }
        return super.preCommit();
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        Widget widget = widgetDs.getItem();
        if (widget.getDashboard() != null) {
            List<Widget> dashboardWidgets = widget.getDashboard().getWidgets();
            long cnt = dashboardWidgets.stream()
                    .filter(w -> !w.getId().equals(widget.getId()) && w.getWidgetId().equals(widget.getWidgetId()))
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