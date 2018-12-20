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

package com.haulmont.addon.dashboard.web.repository;

import com.haulmont.addon.dashboard.model.Parameter;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.model.paramtypes.ParameterValue;
import com.haulmont.addon.dashboard.web.annotation.DashboardWidget;
import com.haulmont.addon.dashboard.web.annotation.WidgetParam;
import com.haulmont.addon.dashboard.web.parametertransformer.ParameterTransformer;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.Resources;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.haulmont.addon.dashboard.web.repository.WidgetRepository.NAME;
import static java.lang.String.format;

@Service(NAME)
public class WidgetRepositoryImpl implements WidgetRepository {

    private static Logger log = LoggerFactory.getLogger(WidgetRepositoryImpl.class);

    @Inject
    private WindowConfig windowConfig;

    @Inject
    private ParameterTransformer parameterTransformer;

    @Inject
    private Metadata metadata;

    @Inject
    private Messages messages;

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
                    try {
                        InputStream resource = resources.getResourceAsStream(windowInfo.getTemplate());
                        if (resource != null) {
                            Document document = documentBuilder.parse(resource);
                            Element window = document.getDocumentElement();
                            String className = window.getAttribute("class");
                            if (StringUtils.isNoneEmpty(className)) {
                                try {
                                    Class clazz = Class.forName(className);
                                    DashboardWidget ann = (DashboardWidget) clazz.getAnnotation(DashboardWidget.class);
                                    if (ann != null) {
                                        String editFrameId = ann.editFrameId();
                                        if (StringUtils.isNotBlank(editFrameId) && !windowConfig.hasWindow(editFrameId)) {
                                            throw new IllegalArgumentException(
                                                    String.format("Unable to find %s edit screen in screen config for widget %s",
                                                            editFrameId, ann.name()));
                                        }
                                        widgetTypeInfos.add(new WidgetTypeInfo(ann.name(), windowInfo.getId(), editFrameId));

                                    }
                                } catch (ClassNotFoundException e) {
                                    log.warn(String.format("Unable to load screen controller class for screen %s, template %s, class %s",
                                            windowInfo.getId(), windowInfo.getTemplate(), className));
                                } catch (Exception e) {
                                    log.error(String.format("Unexpected error on initialization screen %s, template %s, class %s",
                                            windowInfo.getId(), windowInfo.getTemplate(), className), e);
                                }
                            }
                        } else {
                            log.warn(String.format("Unable to load screen template for screen %s, template %s",
                                    windowInfo.getId(), windowInfo.getTemplate()));
                        }
                    } catch (Exception e) {
                        log.error(String.format("Unexpected error on initialization screen %s, template %s",
                                windowInfo.getId(), windowInfo.getTemplate()), e);
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
            Optional<Parameter> fieldValueOpt = widgetFieldValues.stream()
                    .filter(p -> p.getName().equals(parameterField.getName()))
                    .findFirst();
            if (fieldValueOpt.isPresent()) {
                Parameter p = fieldValueOpt.get();
                Object rawValue = parameterTransformer.transform(p.getParameterValue());
                try {
                    FieldUtils.writeField(parameterField, widgetFrame, rawValue, true);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
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
    public void serializeWidgetFields(AbstractFrame widgetFrame, Widget widget) {
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
        String property = format("dashboard-widget.%s", widget.getName());
        String mainMessage = messages.getMainMessage(property);
        return (mainMessage.equals(property) ? widget.getName() : mainMessage);
    }

}
