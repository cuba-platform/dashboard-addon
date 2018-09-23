package com.haulmont.addon.dashboard.web.widgettemplate;

import com.haulmont.addon.dashboard.entity.WidgetTemplate;
import com.haulmont.addon.dashboard.web.dashboard.converter.JsonConverter;
import com.haulmont.addon.dashboard.web.dashboard.tools.AccessConstraintsHelper;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class WidgetTemplateBrowse extends AbstractLookup {
    @Inject
    protected CollectionDatasource<WidgetTemplate, UUID> widgetTemplatesDs;

    @Inject
    protected JsonConverter converter;

    @Inject
    protected Metadata metadata;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    public void createWidgetTemplate() {
        openEditor("dashboard$WidgetTemplate.edit", metadata.create(WidgetTemplate.class), WindowManager.OpenType.NEW_TAB);
    }

    public void editWidgetTemplate() {
        openEditor("dashboard$WidgetTemplate.edit", widgetTemplatesDs.getItem(), WindowManager.OpenType.NEW_TAB);
    }

}