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
import com.haulmont.cuba.core.global.Resources;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
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

    @Inject
    private Resources resources;


    @Override
    public List<WidgetTypeInfo> getWidgetTypesInfo() {
        List<WidgetTypeInfo> result = new ArrayList<>();
        try {
            for (WindowInfo windowInfo : windowConfig.getWindows()) {
                DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                if (StringUtils.isNoneEmpty(windowInfo.getTemplate())) {
                    Document document = documentBuilder.parse(resources.getResourceAsStream(windowInfo.getTemplate()));
                    Element window = document.getDocumentElement();
                    String className = window.getAttribute("class");
                    if (StringUtils.isNoneEmpty(className)) {
                        Class clazz = Class.forName(className);
                        WidgetType ann = (WidgetType) clazz.getAnnotation(WidgetType.class);
                        if (ann != null) {
                            result.add(new WidgetTypeInfo(ann.name(), windowInfo.getId(), ann.editFrameId()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
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
