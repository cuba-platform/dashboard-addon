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

package com.haulmont.addon.dashboard.web.repository;

import com.haulmont.addon.dashboard.model.Parameter;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.model.paramtypes.ParameterValue;
import com.haulmont.addon.dashboard.web.annotation.DashboardWidget;
import com.haulmont.addon.dashboard.web.annotation.WidgetParam;
import com.haulmont.addon.dashboard.web.parametertransformer.ParameterTransformer;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.cuba.gui.screen.ScreenFragment;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component(WidgetRepository.NAME)
public class WidgetRepositoryImpl implements WidgetRepository {

    @Inject
    private WindowConfig windowConfig;

    @Inject
    private ParameterTransformer parameterTransformer;

    @Inject
    private Metadata metadata;

    @Inject
    private Messages messages;

    @Inject
    private Logger log;

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
                    initializeWidgets();
                    initialized = true;
                }
            } finally {
                lock.readLock().lock();
                lock.writeLock().unlock();
            }
        }
    }

    protected void initializeWidgets() {
        widgetTypeInfos = new ArrayList<>();
        for (WindowInfo windowInfo : windowConfig.getWindows()) {
            if (StringUtils.isNotBlank(windowInfo.getTemplate())) {
                Class clazz = windowInfo.getControllerClass();
                if (clazz.isAnnotationPresent(DashboardWidget.class)) {
                    DashboardWidget widgetAnnotation = (DashboardWidget) clazz.getAnnotation(DashboardWidget.class);
                    String editFrameId = widgetAnnotation.editFrameId();
                    if (StringUtils.isNotBlank(editFrameId) && !windowConfig.hasWindow(editFrameId)) {
                        log.error("Unable to find {} edit screen in screen config for widget {}", editFrameId, widgetAnnotation.name());
                        throw new IllegalArgumentException(
                                String.format("Unable to find %s edit screen in screen config for widget %s",
                                        editFrameId, widgetAnnotation.name()));
                    }
                    widgetTypeInfos.add(new WidgetTypeInfo(widgetAnnotation.name(), windowInfo.getId(), editFrameId));
                }
            }
        }
    }

    @Override
    public void initializeWidgetFields(ScreenFragment widgetFrame, Widget widget) {
        List<Parameter> widgetFieldValues = widget.getWidgetFields();
        List<Field> parameterFields = FieldUtils.getFieldsListWithAnnotation(widgetFrame.getClass(), WidgetParam.class);
        for (Field parameterField : parameterFields) {
            Optional<Parameter> fieldValueOpt = widgetFieldValues.stream()
                    .filter(p -> p.getName().equals(parameterField.getName()))
                    .findFirst();
            if (fieldValueOpt.isPresent()) {
                Parameter p = fieldValueOpt.get();
                Object rawValue = parameterTransformer.transform(p.getParameterValue());
                try {
                    FieldUtils.writeField(parameterField, widgetFrame, rawValue, true);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(String.format("Error on widget field %s initialization", p.getAlias()), e);
                }
            }
        }
    }

    @Override
    public Map<String, Object> getWidgetParams(Widget widget) {//
        Map<String, Object> widgetParams = new HashMap<>();

        for (Parameter p : widget.getWidgetFields()) {
            Object rawValue = parameterTransformer.transform(p.getParameterValue());
            widgetParams.put(getAlias(p), rawValue);
        }

        if (widget.getDashboard() != null) {
            for (Parameter p : widget.getDashboard().getParameters()) {
                Object rawValue = parameterTransformer.transform(p.getParameterValue());
                widgetParams.put(getAlias(p), rawValue);
            }
        }

        for (Parameter p : widget.getParameters()) {
            Object rawValue = parameterTransformer.transform(p.getParameterValue());
            widgetParams.put(getAlias(p), rawValue);
        }
        return widgetParams;
    }

    private String getAlias(Parameter p) {
        return p.getAlias() != null ? p.getAlias() : p.getName();
    }

    @Override
    public void serializeWidgetFields(ScreenFragment widgetFrame, Widget widget) {
        widget.getWidgetFields().clear();
        List<Field> parameterFields = FieldUtils.getFieldsListWithAnnotation(widgetFrame.getClass(), WidgetParam.class);
        for (Field parameterField : parameterFields) {
            Parameter parameter = metadata.create(Parameter.class);
            parameter.setName(parameterField.getName());
            parameter.setAlias(parameterField.getName());
            ParameterValue parameterValue = parameterTransformer.createParameterValue(parameterField, widgetFrame);
            parameter.setParameterValue(parameterValue);
            widget.getWidgetFields().add(parameter);
        }
    }

    public String getLocalizedWidgetName(Widget widget) {
        String property = String.format("dashboard-widget.%s", widget.getName());
        String mainMessage = messages.getMainMessage(property);
        return (mainMessage.equals(property) ? widget.getName() : mainMessage);
    }

}
