/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.widget_tempate;

import com.audimex.dashboard.model.Parameter;
import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.web.parameter.ParameterBrowse;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static com.audimex.dashboard.web.parameter.ParameterBrowse.PARAMETERS;

public class WidgetTemplateEdit extends AbstractEditor<Widget> {
    @Inject
    protected Datasource<Widget> widgetDs;
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected ParameterBrowse paramsFrame;
    @Inject
    protected ComponentsFactory componentsFactory;

    protected LookupField frameIdLookup;

    @Override
    protected void postInit() {
        initFrameIdField();

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

    protected void initFrameIdField() {
        FieldGroup.FieldConfig frameIdConfig = fieldGroup.getField("frameId");
        frameIdLookup = componentsFactory.createComponent(LookupField.class);
        frameIdLookup.setDatasource(widgetDs, frameIdConfig.getProperty());
        frameIdLookup.setOptionsList(getFrameIds());
        frameIdConfig.setComponent(frameIdLookup);
    }

    protected List getFrameIds() {
        return Collections.emptyList();
    }
}