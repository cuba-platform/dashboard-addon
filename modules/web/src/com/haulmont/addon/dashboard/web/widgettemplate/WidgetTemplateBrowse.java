package com.haulmont.addon.dashboard.web.widgettemplate;

import com.haulmont.addon.dashboard.entity.WidgetTemplate;
import com.haulmont.addon.dashboard.web.dashboard.converter.JsonConverter;
import com.haulmont.addon.dashboard.web.widgettemplategroup.WidgetTemplateGroupBrowse;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.screen.OpenMode;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

@UiController("dashboard$WidgetTemplate.browse")
@UiDescriptor("widget-template-browse.xml")
public class WidgetTemplateBrowse extends AbstractLookup {
    @Inject
    protected CollectionDatasource<WidgetTemplate, UUID> widgetTemplatesDs;

    @Inject
    protected JsonConverter converter;

    @Inject
    protected Metadata metadata;

    @Inject
    protected Screens screens;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    public void onWidgetTemplateGroupsBrowseClick() {
        screens.create(WidgetTemplateGroupBrowse.class, OpenMode.NEW_TAB).show();
    }
}