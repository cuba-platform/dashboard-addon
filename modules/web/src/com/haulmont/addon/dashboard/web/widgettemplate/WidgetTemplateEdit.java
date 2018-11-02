/*
 * Copyright (c) 2008-2018 Haulmont.
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
 *
 */
package com.haulmont.addon.dashboard.web.widgettemplate;

import com.haulmont.addon.dashboard.entity.WidgetTemplate;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.web.dashboard.converter.JsonConverter;
import com.haulmont.addon.dashboard.web.dashboard.tools.AccessConstraintsHelper;
import com.haulmont.addon.dashboard.web.dashboard.tools.WidgetUtils;
import com.haulmont.addon.dashboard.web.repository.WidgetRepository;
import com.haulmont.addon.dashboard.web.repository.WidgetTypeInfo;
import com.haulmont.addon.dashboard.web.widget.WidgetEdit;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.AbstractDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

import static com.haulmont.cuba.gui.WindowManager.OpenType.THIS_TAB;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class WidgetTemplateEdit extends AbstractEditor<WidgetTemplate> {

    @Named("fieldGroup")
    FieldGroup fieldGroup;

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

    @Inject
    protected AccessConstraintsHelper accessHelper;

    protected LookupField widgetTypeLookup;
    protected Button editWidgetButton;

    protected boolean openWidgetEditor = false;

    @Override
    protected void postInit() {
        super.postInit();
        if (PersistenceHelper.isNew(getItem())) {
            getItem().setCreatedBy(accessHelper.getCurrentSessionLogin());
        }

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
        if (!accessHelper.getCurrentSessionLogin().equals(getItem().getCreatedBy())) {
            fieldGroup.getField("isAvailableForAllUsers").setVisible(false);
        }
        openWidgetEditor = true;
        ((AbstractDatasource) widgetTemplateDs).setModified(false);
    }

    public Component generateWidgetTypeField(Datasource datasource, String fieldId) {
        HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
        hBoxLayout.setSpacing(true);
        LookupField widgetTypeLookup = componentsFactory.createComponent(LookupField.class);
        widgetTypeLookup.setWidth("100%");
        Button editWidgetButton = componentsFactory.createComponent(Button.class);
        editWidgetButton.setWidth("100%");
        editWidgetButton.setCaption(messages.getMessage(WidgetTemplateEdit.class, "customize"));
        editWidgetButton.setIcon("icons/gear.png");
        widgetTypeLookup.setOptionsMap(widgetUtils.getWidgetCaptions());
        widgetTypeLookup.addValueChangeListener(e -> {
            String browserFrameId = (String) e.getValue();
            if (browserFrameId != null) {
                Widget widget = metadata.create(Widget.class);
                widget.setFrameId(browserFrameId);
                widget.setName(widgetUtils.getWidgetType((String) e.getValue()));
                openWidgetEditor(widget);
            }
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
        if (widget == null) {
            lookupField.setValue(null);
            return;
        }
        String browseFrameId = widget.getFrameId();

        Optional<WidgetTypeInfo> widgetTypeOpt = widgetRepository.getWidgetTypesInfo().stream()
                .filter(typeInfo -> browseFrameId.equals(typeInfo.getFrameId()))
                .findFirst();

        if (widgetTypeOpt.isPresent()) {
            String itemCaption = widgetTypeOpt.get().getFrameId();

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