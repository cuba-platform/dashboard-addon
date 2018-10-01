/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.haulmont.addon.dashboard.web.widgettemplate;

import com.haulmont.addon.dashboard.entity.WidgetTemplate;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.web.dashboard.converter.JsonConverter;
import com.haulmont.addon.dashboard.web.dashboard.tools.WidgetUtils;
import com.haulmont.addon.dashboard.web.repository.WidgetRepository;
import com.haulmont.addon.dashboard.web.widget.WidgetEdit;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.ValidationErrors;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;

import static com.haulmont.cuba.gui.WindowManager.OpenType.DIALOG;

public class WidgetTemplateEdit extends AbstractEditor<WidgetTemplate> {

    @Inject
    protected WidgetRepository widgetRepository;

    @Inject
    protected ComponentsFactory componentsFactory;

    @Inject
    protected Datasource<WidgetTemplate> widgetTemplateDs;

    @Inject
    protected JsonConverter converter;

    @Inject
    protected Metadata metadata;

    @Inject
    protected Messages messages;

    @Inject
    protected WidgetUtils widgetUtils;

    protected LookupField widgetTypeLookup;

    @Override
    protected void postInit() {
        super.postInit();
        if (StringUtils.isNotEmpty(widgetTemplateDs.getItem().getWidgetModel())) {
            converter.widgetFromJson(widgetTemplateDs.getItem().getWidgetModel());
        }
    }

    public void openWidgetEditor() {
        WidgetTemplate widgetTemplate = widgetTemplateDs.getItem();
        if (StringUtils.isNotEmpty(widgetTemplate.getWidgetModel())) {
            Widget widget = converter.widgetFromJson(widgetTemplateDs.getItem().getWidgetModel());
            WidgetEdit editor = (WidgetEdit) openEditor(WidgetEdit.SCREEN_NAME, widget, DIALOG);
            editor.addCloseListener((action) -> {
                if (Window.COMMIT_ACTION_ID.equals(action)) {
                    widgetTemplate.setWidgetModel(converter.widgetToJson(widget));
                }
            });
        }
    }


    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        WidgetTemplate widgetTemplate = widgetTemplateDs.getItem();
        if (StringUtils.isEmpty(widgetTemplate.getWidgetModel())) {
            errors.add(widgetTypeLookup, getMessage("emptyWidgetError"));
        }
    }
}