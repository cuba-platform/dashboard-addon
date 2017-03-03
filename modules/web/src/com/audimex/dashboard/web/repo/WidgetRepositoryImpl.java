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

@Component(WidgetRepository.NAME)
public class WidgetRepositoryImpl implements WidgetRepository {
    private final Logger log = LoggerFactory.getLogger(WidgetRepositoryImpl.class);

    private volatile boolean initialized = false;
    private List<Widget> widgetMap;

    @Inject
    private WidgetConfig widgetConfig;

    @Inject
    private Resources resources;

    @Override
    public List<Widget> getWidgets() {
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
            List<Widget> widgets = new ArrayList<>();

            for (String fileName : stringTokenizer.getTokenArray()) {
                List<Widget> widgetsFromFile = readFile(fileName);
                widgets.addAll(widgetsFromFile);
            }

            widgetMap = Collections.unmodifiableList(widgets);
        } else {
            widgetMap = Collections.emptyList();
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

    protected List<Widget> readFile(String fileName) {
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

            List<Widget> widgets = new ArrayList<>();

            for (Element viewElem : Dom4j.elements(rootElem, "widget")) {
                Widget widget = createWidget(viewElem);
                widgets.add(widget);
            }

            return widgets;
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }
}