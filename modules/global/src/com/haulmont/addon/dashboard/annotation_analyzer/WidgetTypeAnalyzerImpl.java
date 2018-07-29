/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.annotation_analyzer;

import com.haulmont.addon.dashboard.annotation.WidgetType;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.global.Metadata;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static com.haulmont.addon.dashboard.annotation_analyzer.WidgetTypeAnalyzer.NAME;

@Service(NAME)
public class WidgetTypeAnalyzerImpl implements WidgetTypeAnalyzer {
    @Inject
    protected Metadata metadata;

    @Override
    public List<WidgetTypeInfo> getWidgetTypesInfo() {
        List<MetaClass> widgetClasses = getWidgetClasses();
        return getWidgetTypesInfo(widgetClasses);
    }

    protected List<WidgetTypeInfo> getWidgetTypesInfo(List<MetaClass> widgetClasses) {
        return widgetClasses.stream()
                .map(metaClass -> {
                    WidgetType ann = (WidgetType) metaClass.getJavaClass().getAnnotation(WidgetType.class);
                    return new WidgetTypeInfo(
                            metaClass.getJavaClass(),
                            ann.name(),
                            ann.browseFrameId(),
                            ann.editFrameId()
                    );
                }).collect(Collectors.toList());
    }

    protected List<MetaClass> getWidgetClasses() {
        return metadata.getSession().getClasses()
                .stream()
                .filter(metaClass -> {
                    Class javaClass = metaClass.getJavaClass();
                    return Widget.class.isAssignableFrom(javaClass) && javaClass.isAnnotationPresent(WidgetType.class);
                })
                .collect(Collectors.toList());
    }
}
