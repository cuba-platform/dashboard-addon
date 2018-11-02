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
