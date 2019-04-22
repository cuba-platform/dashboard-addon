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

package com.haulmont.addon.dashboard.web.widgettemplate;

import com.haulmont.addon.dashboard.entity.WidgetTemplate;
import com.haulmont.addon.dashboard.web.dashboard.converter.JsonConverter;
import com.haulmont.addon.dashboard.web.widgettemplategroup.WidgetTemplateGroupBrowse;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.screen.OpenMode;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

@UiController("dashboard$WidgetTemplate.browse")
@UiDescriptor("widget-template-browse.xml")
public class WidgetTemplateBrowse extends AbstractLookup {
    @Inject
    protected CollectionDatasource<WidgetTemplate, UUID> widgetTemplatesDs;

    @Inject
    protected JsonConverter converter;

    @Inject
    protected Metadata metadata;

    @Inject
    protected Screens screens;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    public void onWidgetTemplateGroupsBrowseClick() {
        screens.create(WidgetTemplateGroupBrowse.class, OpenMode.NEW_TAB).show();
    }
}