/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.parameter.frames;

import com.audimex.dashboard.model.param_value_types.EnumParameterValue;
import com.audimex.dashboard.model.param_value_types.ParameterValue;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.LookupField;

import javax.inject.Inject;
import java.util.*;

import static org.apache.commons.lang.StringUtils.isBlank;

public class EnumValueFrame extends AbstractFrame implements ValueFrame {
    @Inject
    protected LookupField enumClassLookup;
    @Inject
    protected Metadata metadata;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        loadEnumClasses();
        selectIfExist((EnumParameterValue) params.get(VALUE));
    }

    @Override
    public ParameterValue getValue() {
        Class value = enumClassLookup.getValue();
        return new EnumParameterValue(value == null ? null : value.getName());
    }

    protected void loadEnumClasses() {
        List<Class> allEnums = new ArrayList<>(metadata.getTools().getAllEnums());
        enumClassLookup.setOptionsList(allEnums);
    }

    protected void selectIfExist(EnumParameterValue enumValue) {
        if (enumValue == null || isBlank(enumValue.getValue())) {
            return;
        }

        String className = enumValue.getValue();

        ((List<Class>) enumClassLookup.getOptionsList())
                .stream()
                .filter(clazz -> className.equals(clazz.getName()))
                .findFirst()
                .ifPresent(enumClass -> enumClassLookup.setValue(enumClass));

    }


}
