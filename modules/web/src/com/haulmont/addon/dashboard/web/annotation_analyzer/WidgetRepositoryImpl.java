/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.annotation_analyzer;

import com.haulmont.addon.dashboard.model.Parameter;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.model.param_value_types.ParameterValue;
import com.haulmont.addon.dashboard.web.annotation.WidgetParam;
import com.haulmont.addon.dashboard.web.annotation.WidgetType;
import com.haulmont.addon.dashboard.web.parameter_transformer.ParameterTransformer;
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

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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

    protected volatile boolean initialized;
    protected ReadWriteLock lock = new ReentrantReadWriteLock();

    private List<WidgetTypeInfo> widgetTypeInfos;

    @Override
    public List<WidgetTypeInfo> getWidgetTypesInfo() {
        lock.readLock().lock();
        try {
            checkInitialized();
            return Collections.unmodifiableList(widgetTypeInfos);
        } finally {
            lock.readLock().unlock();
        }
    }

    private void checkInitialized() {
        if (!initialized) {
            lock.readLock().unlock();
            lock.writeLock().lock();
            try {
                if (!initialized) {
                    init();
                    initialized = true;
                }
            } finally {
                lock.readLock().lock();
                lock.writeLock().unlock();
            }
        }
    }

    protected void init() {
        widgetTypeInfos = new ArrayList<>();
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
                            widgetTypeInfos.add(new WidgetTypeInfo(ann.name(), windowInfo.getId(), ann.editFrameId()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to initialize widget types", e);
        }
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
    public Map<String, Object> getWidgetParams(Widget widget) {
        Map<String, Object> widgetParams = new HashMap<>();

        for (Parameter p : widget.getWidgetFields()) {
            Object rawValue = parameterTransformer.transform(p.getParameterValue());
            widgetParams.put(p.getName(), rawValue);
        }

        if (widget.getDashboard() != null) {
            for (Parameter p : widget.getDashboard().getParameters()) {
                Object rawValue = parameterTransformer.transform(p.getParameterValue());
                widgetParams.put(p.getName(), rawValue);
            }
        }

        for (Parameter p : widget.getParameters()) {
            Object rawValue = parameterTransformer.transform(p.getParameterValue());
            widgetParams.put(p.getName(), rawValue);
        }
        return widgetParams;
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
