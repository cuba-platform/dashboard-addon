/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
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
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    }

    @Override
    public Widget getItem() {
        Widget item = super.getItem();
        item.setCreatedBy(accessHelper.getCurrentSessionLogin());
        return item;
    }

    protected void setWidgetType() {
        String browseFrameId = widgetDs.getItem().getBrowseFrameId();

        WidgetTypeInfo widgetType = typesInfo.stream()
                .filter(typeInfo -> browseFrameId.equals(typeInfo.getBrowseFrameId()))
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
                    .filter(
                            w -> !w.getId().equals(inputItem.getId()) && w.getWidgetId().equals(inputItem.getWidgetId()))
                    .count();
            if (cnt > 0) {
                errors.add(fieldGroup.getComponent("widgetId"), getMessage("uniqueWidgetId"));
            }
        }
    }

}