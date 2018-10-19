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

package com.haulmont.addon.dashboard.web.repository;


import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.cuba.gui.components.AbstractFrame;

import java.util.List;
import java.util.Map;

/**
 * Scanning the project for use classes with the annotation {@link Widget} and
 * provides information {@link WidgetTypeInfo} about these classes
 */
public interface WidgetRepository {
    String NAME = "dashboard_WidgetRepository";

    List<WidgetTypeInfo> getWidgetTypesInfo();

    void initializeWidgetFields(AbstractFrame widgetFrame, Widget widget);

    void serializeWidgetFields(AbstractFrame widgetFrame, Widget widget);

    Map<String, Object> getWidgetParams(Widget widget);

    String getLocalizedWidgetName(Widget widget);
}
