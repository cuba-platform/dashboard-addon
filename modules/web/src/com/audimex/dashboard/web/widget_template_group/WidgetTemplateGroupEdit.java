/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.widget_template_group;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.audimex.dashboard.entity.WidgetTemplateGroup;
import com.haulmont.cuba.gui.components.actions.AddAction;

import javax.inject.Named;
import java.util.Map;

public class WidgetTemplateGroupEdit extends AbstractEditor<WidgetTemplateGroup> {
    @Named("widgetTemplatesTable.add")
    protected AddAction add;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        add.setWindowId("widgetTemplateBrowse");
    }
}