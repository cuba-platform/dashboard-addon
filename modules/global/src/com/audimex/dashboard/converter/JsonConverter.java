/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.converter;

import com.audimex.dashboard.model.Dashboard;
import com.audimex.dashboard.model.param_types.Value;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonConverter {

    protected Gson gson;

    public JsonConverter() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Value.class, new ValueAdapter());
        Gson gson = builder.create();
    }

    public String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public Dashboard fromJson(String json) {
        return gson.fromJson(json, Dashboard.class);
    }
}
