/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.haulmont.addon.dashboard.web.widgettemplate;

import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.web.dashboard.converter.JsonConverter;
import com.haulmont.addon.dashboard.web.dashboard.tools.WidgetUtils;
import com.haulmont.addon.dashboard.web.repository.WidgetRepository;
import com.haulmont.addon.dashboard.web.repository.WidgetTypeInfo;
import com.haulmont.addon.dashboard.web.widget.WidgetEdit;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.addon.dashboard.entity.WidgetTemplate;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.haulmont.cuba.gui.WindowManager.OpenType.THIS_TAB;
import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isNotBlank;

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
    protected Button editWidgetButton;

    protected boolean openWidgetEditor = false;

    @Override
    protected void postInit() {
        super.postInit();
        if (StringUtils.isNotEmpty(widgetTemplateDs.getItem().getWidgetModel())) {
            Widget widget = converter.widgetFromJson(widgetTemplateDs.getItem().getWidgetModel());
            setWidgetTypeLookupValue(widget, widgetTypeLookup);
        }
        editWidgetButton.setAction(new BaseAction("openWidgetEditor") {
            @Override
            public void actionPerform(Component component) {
                WidgetTemplate widgetTemplate = widgetTemplateDs.getItem();
                if (StringUtils.isNotEmpty(widgetTemplate.getWidgetModel())) {
                    Widget widget = converter.widgetFromJson(widgetTemplateDs.getItem().getWidgetModel());
                    openWidgetEditor(widget);
                }
            }
        });
        openWidgetEditor = true;
    }

    public Component generateWidgetTypeField(Datasource datasource, String fieldId) {
        HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
        LookupField widgetTypeLookup = componentsFactory.createComponent(LookupField.class);
        widgetTypeLookup.setWidth("100%");
        Button editWidgetButton = componentsFactory.createComponent(Button.class);
        editWidgetButton.setWidth("100%");
        editWidgetButton.setCaption(messages.getMessage(WidgetTemplateEdit.class, "edit"));
        widgetTypeLookup.setOptionsMap(widgetUtils.getWidgetCaptions());
        widgetTypeLookup.addValueChangeListener(e -> {
            Widget widget = metadata.create(Widget.class);
            widget.setBrowseFrameId((String) e.getValue());
            widget.setName(widgetUtils.getWidgetType((String) e.getValue()));
            openWidgetEditor(widget);
        });
        this.widgetTypeLookup = widgetTypeLookup;
        this.editWidgetButton = editWidgetButton;
        hBoxLayout.add(widgetTypeLookup);
        hBoxLayout.add(editWidgetButton);
        return hBoxLayout;
    }

    protected void openWidgetEditor(Widget widget) {
        if (openWidgetEditor) {
            WidgetEdit editor = (WidgetEdit) openEditor(WidgetEdit.SCREEN_NAME, widget, THIS_TAB);
            editor.addCloseListener((action) -> {
                if (Window.COMMIT_ACTION_ID.equals(action)) {
                    WidgetTemplate widgetTemplate = widgetTemplateDs.getItem();
                    widgetTemplate.setWidgetModel(converter.widgetToJson(widget));
                } else {
                    Widget prevWidget = converter.widgetFromJson(widgetTemplateDs.getItem().getWidgetModel());
                    openWidgetEditor = false;
                    setWidgetTypeLookupValue(prevWidget, widgetTypeLookup);
                    openWidgetEditor = true;
                }
            });

        }
    }

    protected void setWidgetTypeLookupValue(Widget widget, LookupField lookupField) {
        String browseFrameId = widget.getBrowseFrameId();

        Optional<WidgetTypeInfo> widgetTypeOpt = widgetRepository.getWidgetTypesInfo().stream()
                .filter(typeInfo -> browseFrameId.equals(typeInfo.getBrowseFrameId()))
                .findFirst();

        if (widgetTypeOpt.isPresent()) {
            String itemCaption = widgetTypeOpt.get().getBrowseFrameId();

            if (isNotBlank(itemCaption)) {
                lookupField.setValue(itemCaption);
            }
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