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

package com.haulmont.addon.dashboard.web.dashboard.converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.Parameter;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.model.paramtypes.ParameterValue;
import com.haulmont.addon.dashboard.model.visualmodel.DashboardLayout;
import org.springframework.stereotype.Component;

import static com.haulmont.addon.dashboard.web.dashboard.converter.JsonConverter.NAME;

/**
 * Supports the conversion to json for non-persistent objects with types: {@link Dashboard},
 * {@link Widget}, {@link Parameter}
 */
@Component(NAME)
public class JsonConverter {
    public static final String NAME = "dashboard_JsonConverter";

    protected Gson gson;

    public JsonConverter() {
        GsonBuilder builder = new GsonBuilder();
        builder.setExclusionStrategies(new AnnotationExclusionStrategy());
        builder.registerTypeAdapter(ParameterValue.class, new InheritanceAdapter());
        builder.registerTypeAdapter(DashboardLayout.class, new InheritanceAdapter());
        gson = builder.create();
    }

    public String widgetToJson(Widget widget) {
        return gson.toJson(widget, new TypeToken<Widget>() {
        }.getType());
    }

    public String dashboardToJson(Dashboard dashboard) {
        return gson.toJson(dashboard, new TypeToken<Dashboard>() {
        }.getType());
    }

    public Dashboard dashboardFromJson(String json) {
        return gson.fromJson(json, Dashboard.class);
    }

    public Widget widgetFromJson(String json) {
        return gson.fromJson(json, Widget.class);
    }

    public Parameter parameterFromJson(String json) {
        return gson.fromJson(json, Parameter.class);
    }
}
