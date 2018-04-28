/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.converter;

import com.audimex.dashboard.model.Dashboard;
import com.audimex.dashboard.model.Parameter;
import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.model.param_value_types.Value;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Component;

import static com.audimex.dashboard.converter.JsonConverter.NAME;

@Component(NAME)
public class JsonConverter {
    public static final String NAME = "amxd_JsonConverter";

    protected Gson gson;

    public JsonConverter() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Value.class, new InheritanceAdapter());
        builder.registerTypeAdapter(Widget.class, new InheritanceAdapter());
        gson = builder.create();
    }

    public String toJson(Object obj) {
        return gson.toJson(obj);
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
