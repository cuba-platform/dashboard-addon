/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.widget_tempate;

import com.audimex.dashboard.annotation_analyzer.WidgetTypeAnalyzer;
import com.audimex.dashboard.annotation_analyzer.WidgetTypeInfo;
import com.audimex.dashboard.model.Parameter;
import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.web.parameter.ParameterBrowse;
import com.google.common.collect.Maps;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.audimex.dashboard.web.parameter.ParameterBrowse.PARAMETERS;

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

    protected Map<String, WidgetTypeInfo> typesMap;
    protected AbstractFrame widgetEditFrame;

    @Override
    protected void postInit() {
        typesMap = getWidgetTypesMap();
        typeLookup.setOptionsList(new ArrayList(typesMap.keySet()));
        typeLookup.addValueChangeListener(e -> typeSelected((String)e.getValue()));
        initParametersFrame();

    }

    protected Map<String, WidgetTypeInfo> getWidgetTypesMap() {
        return Maps.uniqueIndex(typeAnalyzer.getWidgetTypesInfo(), WidgetTypeInfo::getCaption);
    }

    protected void typeSelected(String type) {
        WidgetTypeInfo typeInfo = typesMap.get(type);
        String editFrameId = typeInfo.getEditFrameId();
        widgetEditFrame = openFrame(widgetEditBox, editFrameId, ParamsMap.of(ITEM_DS, widgetDs));
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
        widgetDs.getItem().setParameters(parameters);
        return super.preCommit();
    }
}