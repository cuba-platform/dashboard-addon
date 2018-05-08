/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.widget_tempate;

import com.audimex.dashboard.annotation_analyzer.WidgetTypeAnalyzer;
import com.audimex.dashboard.annotation_analyzer.WidgetTypeInfo;
import com.audimex.dashboard.model.Parameter;
import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.web.parameter.ParameterBrowse;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.audimex.dashboard.web.parameter.ParameterBrowse.PARAMETERS;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class WidgetTemplateEdit extends AbstractEditor<Widget> {
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

    //The AbstractEditor replaces an item to another object, if one has status '[new]'
    @WindowParam(name = "ITEM", required = true)
    protected Widget inputItem;

    protected List<WidgetTypeInfo> typesInfo;
    protected AbstractFrame widgetEditFrame;

    @Override
    public void postInit() {
        widgetDs.setItem(inputItem);

        typesInfo = typeAnalyzer.getWidgetTypesInfo();
        typeLookup.setOptionsList(getWidgetCaptions(typesInfo));
        typeLookup.addValueChangeListener(e -> typeSelected((String) e.getValue()));
        selectType();
        initParametersFrame();
    }

    protected List<String> getWidgetCaptions(List<WidgetTypeInfo> typesInfo) {
        return typesInfo.stream()
                .map(WidgetTypeInfo::getCaption)
                .collect(Collectors.toList());
    }

    protected void typeSelected(String type) {
        String editFrameId = typesInfo.stream()
                .filter(widgetType -> type.equals(widgetType.getCaption()))
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
            String itemCaption = widgetTypeOpt.get().getCaption();

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