/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.config.widget_types_config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.haulmont.cuba.core.config.type.TypeFactory;
import com.haulmont.cuba.core.config.type.TypeStringify;

public class WidgetTypesConverter {

    public static Gson generateGson(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.create();
    }

    public static class Factory extends TypeFactory {
        @Override
        public Object build(String string) {
            return generateGson().fromJson(string, WidgetTypesInfo.class);
        }
    }

    public static class Stringify extends TypeStringify {
        @Override
        public String stringify(Object value) {
            return generateGson().toJson(value, WidgetTypesInfo.class);
        }
    }
}
