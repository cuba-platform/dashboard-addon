/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.widget;

import com.audimex.dashboard.annotation_analyzer.WidgetTypeAnalyzer;
import com.audimex.dashboard.annotation_analyzer.WidgetTypeInfo;
import com.audimex.dashboard.model.Parameter;
import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.web.parameter.ParameterBrowse;
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

import static com.audimex.dashboard.web.parameter.ParameterBrowse.PARAMETERS;
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
    protected WidgetTypeAnalyzer typeAnalyzer;
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

        typesInfo = typeAnalyzer.getWidgetTypesInfo();
        typeLookup.setOptionsMap(getWidgetCaptions(typesInfo));
        typeLookup.addValueChangeListener(e -> typeSelected((String) e.getValue()));
        selectType();
        initParametersFrame();
    }

    protected Map<String, String> getWidgetCaptions(List<WidgetTypeInfo> typesInfo) {
        Map<String, String> map = new HashMap<>();

        for (WidgetTypeInfo typeInfo : typesInfo) {
            String name = typeInfo.getName();
            String property = format("widgetType.%s", name);
            String mainMessage = messages.getMainMessage(property);
            String caption = mainMessage.equals(property) ? name : mainMessage;

            map.put(caption, name);
        }

        return map;
    }

    protected void typeSelected(String type) {
        String editFrameId = typesInfo.stream()
                .filter(widgetType -> type.equals(widgetType.getName()))
                .findFirst()
                .get()
                .getEditFrameId();

        widgetEditFrame = openFrame(widgetEditBox, editFrameId, ParamsMap.of(ITEM_DS, widgetDs));
    }

    protected void selectType() {
        Class<? extends Widget> itemClass = widgetDs.getItem().getClass();

        Optional<WidgetTypeInfo> widgetTypeOpt = typesInfo.stream()
                .filter(typeInfo -> itemClass.equals(typeInfo.getTypeClass()))
                .findFirst();

        if (widgetTypeOpt.isPresent()) {
            String itemCaption = widgetTypeOpt.get().getName();

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
        return super.preCommit();
    }
}