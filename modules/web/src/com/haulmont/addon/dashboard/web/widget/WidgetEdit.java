/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.haulmont.addon.dashboard.web.widget;

import com.haulmont.addon.dashboard.model.Parameter;
import com.haulmont.addon.dashboard.web.annotation_analyzer.WidgetRepository;
import com.haulmont.addon.dashboard.web.annotation_analyzer.WidgetTypeInfo;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.web.dashboard.tools.AccessConstraintsHelper;
import com.haulmont.addon.dashboard.web.parameter.ParameterBrowse;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.haulmont.addon.dashboard.web.parameter.ParameterBrowse.PARAMETERS;
import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class WidgetEdit extends AbstractEditor<Widget> {
    public static final String SCREEN_NAME = "widgetEdit";
    public static final String ITEM_DS = "ITEM_DS";

    @Inject
    protected Datasource<Widget> widgetDs;
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected LookupField typeLookup;
    @Inject
    protected VBoxLayout widgetEditBox;
    @Inject
    protected ParameterBrowse paramsFrame;
    @Inject
    protected WidgetRepository widgetRepository;
    @Inject
    protected AccessConstraintsHelper accessHelper;
    @Inject
    protected Messages messages;

    //The AbstractEditor replaces an item to another object, if one has status '[new]'
    @WindowParam(name = "ITEM", required = true)
    protected Widget inputItem;

    protected List<WidgetTypeInfo> typesInfo;
    protected AbstractFrame widgetEditFrame;

    @Override
    public void postInit() {
        widgetDs.setItem(inputItem);

        typesInfo = widgetRepository.getWidgetTypesInfo();
        typeLookup.setOptionsMap(getWidgetCaptions(typesInfo));
        typeLookup.addValueChangeListener(e -> typeSelected((String) e.getValue()));
        selectType();
        initParametersFrame();
    }

    @Override
    public Widget getItem() {
        Widget item = super.getItem();
        item.setCreatedBy(accessHelper.getCurrentSessionLogin());
        return item;
    }

    protected Map<String, String> getWidgetCaptions(List<WidgetTypeInfo> typesInfo) {
        Map<String, String> map = new HashMap<>();

        for (WidgetTypeInfo typeInfo : typesInfo) {
            String browseFrameId = typeInfo.getBrowseFrameId();
            String name = typeInfo.getName();
            String property = format("widgetType.%s", name);
            String mainMessage = messages.getMainMessage(property);
            String caption = mainMessage.equals(property) ? name : mainMessage;

            map.put(caption, browseFrameId);
        }

        return map;
    }

    protected void typeSelected(String type) {
        String editFrameId = typesInfo.stream()
                .filter(widgetType -> type.equals(widgetType.getBrowseFrameId()))
                .findFirst()
                .get()
                .getEditFrameId();

        Map<String, Object> params = new HashMap<>(ParamsMap.of(ITEM_DS, widgetDs));
        params.putAll(widgetRepository.getWidgetParams(widgetDs.getItem()));
        widgetEditFrame = openFrame(widgetEditBox, editFrameId, params);
//        widgetRepository.initializeWidgetFields(widgetEditFrame, widgetDs.getItem());
    }

    protected void selectType() {
        String browseFrameId = widgetDs.getItem().getBrowseFrameId();

        Optional<WidgetTypeInfo> widgetTypeOpt = typesInfo.stream()
                .filter(typeInfo -> browseFrameId.equals(typeInfo.getBrowseFrameId()))
                .findFirst();

        if (widgetTypeOpt.isPresent()) {
            String itemCaption = widgetTypeOpt.get().getBrowseFrameId();

            if (isNotBlank(itemCaption)) {
                typeLookup.setValue(itemCaption);
            }
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
        widgetRepository.serializeWidgetFields(widgetEditFrame, inputItem);
        return super.preCommit();
    }
}