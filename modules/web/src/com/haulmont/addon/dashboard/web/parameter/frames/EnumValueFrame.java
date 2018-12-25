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

package com.haulmont.addon.dashboard.web.parameter.frames;

import com.haulmont.addon.dashboard.model.paramtypes.EnumParameterValue;
import com.haulmont.addon.dashboard.model.paramtypes.ParameterValue;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

@UiController("dashboard$EnumValueFrame")
@UiDescriptor("enum-value-frame.xml")
public class EnumValueFrame extends AbstractFrame implements ValueFrame {
    @Inject
    protected LookupField<Class> enumClassLookup;
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

        enumClassLookup.getOptions().getOptions()
                .filter(clazz -> className.equals(clazz.getName()))
                .findFirst()
                .ifPresent(enumClass -> enumClassLookup.setValue(enumClass));

    }


}
