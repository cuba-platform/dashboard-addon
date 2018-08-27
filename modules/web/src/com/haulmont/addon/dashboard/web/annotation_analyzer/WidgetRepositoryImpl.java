/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.annotation_analyzer;

import com.haulmont.addon.dashboard.model.Parameter;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.model.param_value_types.ParameterValue;
import com.haulmont.addon.dashboard.web.parameter_transformer.ParameterTransformer;
import com.haulmont.addon.dashboard.web.annotation.WidgetParam;
import com.haulmont.addon.dashboard.web.annotation.WidgetType;
import com.haulmont.addon.dashboard.web.widget_types.lookup.LookupWidgetBrowse;
import com.haulmont.addon.dashboard.web.widget_types.screen.ScreenWidgetBrowse;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.config.WindowConfig;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.haulmont.addon.dashboard.web.annotation_analyzer.WidgetRepository.NAME;

@Service(NAME)
public class WidgetRepositoryImpl implements WidgetRepository {

    @Inject
    private WindowConfig windowConfig;

    @Inject
    private ParameterTransformer parameterTransformer;

    @Inject
    private Metadata metadata;

    @Override
    public List<WidgetTypeInfo> getWidgetTypesInfo() {//TODO: how get window class

        List<WidgetTypeInfo> rl = new ArrayList<>();
        WidgetTypeInfo screen = new  WidgetTypeInfo("Screen", "screenWidgetBrowse", "screenWidgetEdit");
        WidgetTypeInfo lookUp = new  WidgetTypeInfo("Lookup", "lookupWidgetBrowse", "lookupWidgetEdit");
        rl.add(screen);
        rl.add(lookUp);
        return rl;

        /*return windowConfig.getWindows().stream().filter(wi -> wi.getScreenClass().getAnnotation(WidgetType.class) != null)
                .map(wi -> {
                    WidgetType ann = (WidgetType) wi.getScreenClass().getAnnotation(WidgetType.class);
                    return new WidgetTypeInfo(ann.name(), wi.getId(), ann.editFrameId());
                }).collect(Collectors.toList());*/
    }

    @Override
    public void initializeWidgetFields(AbstractFrame widgetFrame, Widget widget) {
        List<Parameter> widgetFieldValues = widget.getWidgetFields();
        List<Field> parameterFields = FieldUtils.getFieldsListWithAnnotation(widgetFrame.getClass(), WidgetParam.class);
        for (Field parameterField : parameterFields) {
            Optional<Parameter> fieldValueOpt = widgetFieldValues.stream().filter(p -> p.getName().equals(parameterField.getName())).findFirst();
            if (fieldValueOpt.isPresent()) {
                Parameter p = fieldValueOpt.get();
                WidgetParam ann = parameterField.getAnnotation(WidgetParam.class);
                if (parameterTransformer.compareParameterTypes(ann.type(), parameterField)) {
                    Object rawValue = parameterTransformer.transform(p.getParameterValue());
                    try {
                        FieldUtils.writeField(parameterField, widgetFrame, rawValue, true);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        }
    }

    @Override
    public void serializeWidgetFields(AbstractFrame widgetFrame, Widget widget) {
        widget.getWidgetFields().clear();
        List<Field> parameterFields = FieldUtils.getFieldsListWithAnnotation(widgetFrame.getClass(), WidgetParam.class);
        for (Field parameterField : parameterFields) {
            WidgetParam ann = parameterField.getAnnotation(WidgetParam.class);
            if (parameterTransformer.compareParameterTypes(ann.type(), parameterField)) {
                Parameter parameter = metadata.create(Parameter.class);
                parameter.setName(parameterField.getName());
                ParameterValue parameterValue = parameterTransformer.createParameterValue(ann.type(), parameterField, widgetFrame);
                parameter.setParameterValue(parameterValue);
                widget.getWidgetFields().add(parameter);
            }

        }
    }
}
