package com.haulmont.addon.dashboard.web.dashboard.converter;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.haulmont.addon.dashboard.model.json.Exclude;

public class AnnotationExclusionStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return f.getAnnotation(Exclude.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}