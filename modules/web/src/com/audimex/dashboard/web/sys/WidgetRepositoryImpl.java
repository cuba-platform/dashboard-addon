/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.sys;

import com.audimex.dashboard.entity.DashboardWidget;
import com.audimex.dashboard.web.WidgetConfig;
import com.audimex.dashboard.web.WidgetRepository;
import com.haulmont.bali.util.Dom4j;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Resources;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrTokenizer;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component(WidgetRepository.NAME)
public class WidgetRepositoryImpl implements WidgetRepository {
    private final Logger log = LoggerFactory.getLogger(WidgetRepositoryImpl.class);

    protected volatile boolean initialized = false;
    protected List<DashboardWidget> predefinedWidgetMap = new ArrayList<>();
    protected List<DashboardWidget> loadedWidgetsMap = new ArrayList<>();

    @Inject
    protected WidgetConfig widgetConfig;

    @Inject
    protected DataManager dataManager;

    @Inject
    protected Resources resources;

    @Override
    public List<DashboardWidget> getWidgets() {
        checkInitialized();

        List<DashboardWidget> widgetMap = new ArrayList<>();
        widgetMap.addAll(predefinedWidgetMap);
        widgetMap.addAll(loadedWidgetsMap);
        return widgetMap;
    }

    @Override
    public List<DashboardWidget> getWidgets(String entityType) {
        checkInitialized();

        List<DashboardWidget> widgetMap = new ArrayList<>();
        widgetMap.addAll(
                predefinedWidgetMap.stream()
                        .filter(widget -> widget.getEntityType() == null || widget.getEntityType().equals(entityType))
                        .collect(Collectors.toList())
        );
        widgetMap.addAll(
                loadedWidgetsMap.stream()
                        .filter(widget -> widget.getEntityType() == null || widget.getEntityType().equals(entityType))
                        .collect(Collectors.toList())
                );
        return widgetMap;
    }

    protected void checkInitialized() {
        if (!initialized) {
            synchronized (this) {
                if (!initialized) {
                    log.debug("Loading dashboard widgets");
                    init();
                    initialized = true;
                }
            }
        }

        LoadContext<DashboardWidget> ctx = LoadContext.create(DashboardWidget.class)
                .setQuery(LoadContext.createQuery("select dw from amxd$DashboardWidget dw"))
                .setView("dashboardWidget-view");
        loadedWidgetsMap.clear();
        loadedWidgetsMap = dataManager.loadList(ctx);
    }

    protected void init() {
        String paths = widgetConfig.getWidgetConfigPaths();
        if (StringUtils.isNotBlank(paths)) {
            StrTokenizer stringTokenizer = new StrTokenizer(paths);

            for (String fileName : stringTokenizer.getTokenArray()) {
                List<DashboardWidget> widgetsFromFile = readFile(fileName);
                predefinedWidgetMap.addAll(widgetsFromFile);
            }
        } else {
            predefinedWidgetMap = Collections.emptyList();
        }
    }

    protected DashboardWidget createWidget(Element viewElem) {
        DashboardWidget widget = new DashboardWidget();

        String viewId = viewElem.attributeValue("id");
        if (StringUtils.isNotEmpty(viewId)) {
            widget.setWidgetId(viewId);
        }

        String viewName = viewElem.attributeValue("caption");
        if (StringUtils.isNotEmpty(viewName)) {
            widget.setCaption(viewName);
        }

        String viewIcon = viewElem.attributeValue("icon");
        if (StringUtils.isNotEmpty(viewIcon)) {
            widget.setIcon(viewIcon);
        }

        String viewDescription = viewElem.attributeValue("description");
        if (StringUtils.isNotEmpty(viewDescription)) {
            widget.setDescription(viewDescription);
        }

        String viewFrameId = viewElem.attributeValue("frameId");
        if (StringUtils.isNotEmpty(viewFrameId)) {
            widget.setFrameId(viewFrameId);
        }

        return widget;
    }

    protected List<DashboardWidget> readFile(String fileName) {
        log.debug("Deploying widgets config: " + fileName);

        InputStream stream = null;
        try {
            stream = resources.getResourceAsStream(fileName);
            if (stream == null) {
                throw new IllegalStateException("Resource is not found: " + fileName);
            }

            SAXReader reader = new SAXReader();
            Document doc;
            try {
                doc = reader.read(new InputStreamReader(stream, "UTF-8"));
            } catch (Exception e) {
                throw new RuntimeException("Unable to parse view file " + fileName, e);
            }
            Element rootElem = doc.getRootElement();

            List<DashboardWidget> widgets = new ArrayList<>();

            for (Element viewElem : Dom4j.elements(rootElem, "widget")) {
                DashboardWidget widget = createWidget(viewElem);
                widgets.add(widget);
            }

            return widgets;
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }
}