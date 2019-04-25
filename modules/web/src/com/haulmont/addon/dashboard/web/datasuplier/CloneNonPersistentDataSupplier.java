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

package com.haulmont.addon.dashboard.web.datasuplier;

import com.haulmont.addon.dashboard.model.Parameter;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.MetadataTools;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.data.impl.GenericDataSupplier;

import javax.annotation.Nullable;

public class CloneNonPersistentDataSupplier extends GenericDataSupplier {

    @Override
    public <E extends Entity> E reload(E entity, View view, @Nullable MetaClass metaClass, boolean loadDynamicAttributes) {
        MetadataTools metadataTools = AppBeans.get(MetadataTools.class);
        E clone = metadataTools.deepCopy(entity);

        if (entity instanceof Parameter) {
            Parameter parEntity = (Parameter) entity;
            Parameter parClone = (Parameter) clone;
            parClone.setParameterValue(parEntity.getParameterValue());
        }
        if (entity instanceof Widget) {
            Widget widgetEntity = (Widget) entity;
            Widget widgetClone = (Widget) clone;
            for (Parameter p : widgetEntity.getParameters()) {
                Parameter clonePar = widgetClone.getParameters().stream()
                        .filter(p::equals).findFirst()
                        .orElseThrow(() -> new RuntimeException("Can't find parameter " + p.getName()));
                clonePar.setParameterValue(p.getParameterValue());
            }

            for (Parameter p : widgetEntity.getWidgetFields()) {
                Parameter clonePar = widgetClone.getWidgetFields().stream()
                        .filter(p::equals).findFirst()
                        .orElseThrow(() -> new RuntimeException("Can't find widget field " + p.getName()));
                clonePar.setParameterValue(p.getParameterValue());
            }

            if (widgetEntity.getDashboard() != null) {
                for (Parameter p : widgetEntity.getDashboard().getParameters()) {
                    Parameter clonePar = widgetClone.getDashboard().getParameters().stream()
                            .filter(p::equals).findFirst()
                            .orElseThrow(() -> new RuntimeException("Can't find dashboard parameter " + p.getName()));
                    clonePar.setParameterValue(p.getParameterValue());
                }
            }
        }
        return clone;
    }
}
