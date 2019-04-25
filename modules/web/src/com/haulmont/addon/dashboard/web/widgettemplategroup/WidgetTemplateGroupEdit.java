/*
 * Copyright (c) 2008-2019 Haulmont.
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
 */
package com.haulmont.addon.dashboard.web.widgettemplategroup;

import com.haulmont.addon.dashboard.entity.WidgetTemplateGroup;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.actions.AddAction;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

import javax.inject.Named;
import java.util.Map;

@UiController("dashboard$WidgetTemplateGroup.edit")
@UiDescriptor("widget-template-group-edit.xml")
public class WidgetTemplateGroupEdit extends AbstractEditor<WidgetTemplateGroup> {
    @Named("widgetTemplatesTable.add")
    protected AddAction add;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        add.setWindowId("dashboard$WidgetTemplate.browse");
    }
}