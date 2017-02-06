/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.repo;

import com.audimex.dashboard.web.WidgetConfig;
import com.haulmont.bali.util.Dom4j;
import com.haulmont.cuba.core.global.Resources;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrTokenizer;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Component(WidgetRepository.NAME)
public class WidgetRepositoryImpl implements WidgetRepository {
    protected volatile boolean initialized = false;
    protected Map<String, Widget> widgetMap = new LinkedHashMap<>(); // widget map, LinkedHashMap
    protected Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    protected WidgetConfig widgetConfig;

    @Inject
    protected Resources resources;

    @Override
    public Map<String, Widget> getWidgets() {
        checkInitialized();
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
    }

    protected void init() {
        String paths = widgetConfig.getWidgetConfigPaths();
        if (StringUtils.isNotBlank(paths)) {
            StrTokenizer stringTokenizer = new StrTokenizer(paths);
            Map<String, Widget> widgets = new LinkedHashMap<>();

            for (String fileName : stringTokenizer.getTokenArray()) {
                Map<String, Widget> widgetsFromFile = readFile(fileName);
                widgets.putAll(widgetsFromFile);
            }

            widgetMap = Collections.unmodifiableMap(widgets);
        } else {
            widgetMap = Collections.emptyMap();
        }
    }

    protected Widget createWidget(Element viewElem) {
        Widget widget = new Widget();

        String viewId = viewElem.attributeValue("id");
        if (StringUtils.isNotEmpty(viewId)) {
            widget.setId(viewId);
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

    protected Map<String, Widget> readFile(String fileName) {
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
                doc = reader.read(new InputStreamReader(stream));
            } catch (DocumentException e) {
                throw new RuntimeException("Unable to parse view file " + fileName, e);
            }
            Element rootElem = doc.getRootElement();

            Map<String, Widget> widgets = new LinkedHashMap<>();

            for (Element viewElem : Dom4j.elements(rootElem, "widget")) {
                Widget widget = createWidget(viewElem);
                widgets.put(widget.getId(), widget);
            }

            return widgets;
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }
}