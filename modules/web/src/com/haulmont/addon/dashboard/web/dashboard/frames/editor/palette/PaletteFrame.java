/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.frames.editor.palette;

import com.haulmont.addon.dashboard.annotation_analyzer.WidgetTypeAnalyzer;
import com.haulmont.addon.dashboard.converter.JsonConverter;
import com.haulmont.addon.dashboard.entity.WidgetTemplate;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.web.dashboard.events.DoWidgetTemplateEvent;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.vaadin_components.PaletteButton;
import com.haulmont.addon.dashboard.web.dashboard.tools.AccessConstraintsHelper;
import com.haulmont.addon.dashboard.web.dashboard.tools.component_factories.PaletteComponentsFactory;
import com.haulmont.addon.dashboard.web.dashboard.tools.drop_handlers.NotDropHandler;
import com.haulmont.addon.dnd.components.DDVerticalLayout;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.addon.dashboard.web.dashboard.events.DoWidgetTemplateEvent;
import com.haulmont.addon.dashboard.web.dashboard.tools.AccessConstraintsHelper;
import com.haulmont.addon.dashboard.web.dashboard.tools.component_factories.PaletteComponentsFactory;
import com.haulmont.addon.dashboard.web.dashboard.tools.drop_handlers.NotDropHandler;
import org.springframework.context.event.EventListener;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PaletteFrame extends AbstractFrame {
    public static final String SCREEN_NAME = "paletteFrame";

    @Inject
    protected DDVerticalLayout ddWidgetBox;
    @Inject
    protected DDVerticalLayout ddLayoutBox;
    @Inject
    protected DDVerticalLayout ddWidgetTemplateBox;
    @Inject
    protected CollectionDatasource<WidgetTemplate, UUID> widgetTemplatesDs;
    @Inject
    protected Metadata metadata;
    @Inject
    protected PaletteComponentsFactory factory;
    @Inject
    protected JsonConverter converter;
    @Inject
    protected AccessConstraintsHelper accessHelper;
    @Inject
    protected WidgetTypeAnalyzer typeAnalyzer;

    @Override
    public void init(Map<String, Object> params) {
        initWidgetBox();
        initLayoutBox();
        initWidgetTemplateBox();
    }

    @EventListener
    public void onDoTemplate(DoWidgetTemplateEvent event) {
        Widget widget = event.getSource();
        WidgetTemplate widgetTemplate = metadata.create(WidgetTemplate.class);
        widget.setId(widgetTemplate.getId());
        widgetTemplate.setWidgetModel(converter.widgetToJson(widget));

        widgetTemplatesDs.addItem(widgetTemplate);
        widgetTemplatesDs.commit();
        widgetTemplatesDs.refresh();
    }

    protected void initWidgetBox() {
        ddWidgetBox.setDropHandler(new NotDropHandler());

        for (Widget widget : getSketchWidgets()) {
            PaletteButton widgetBtn = factory.createWidgetButton(widget);
            ddWidgetBox.add(widgetBtn);
        }
    }

    protected void initLayoutBox() {
        ddLayoutBox.setDropHandler(new NotDropHandler());

        ddLayoutBox.add(factory.createVerticalLayoutButton());
        ddLayoutBox.add(factory.createHorizontalLayoutButton());
        ddLayoutBox.add(factory.createGridLayoutButton());
    }

    protected void initWidgetTemplateBox() {
        ddWidgetTemplateBox.setDropHandler(new NotDropHandler());
        widgetTemplatesDs.addCollectionChangeListener(e -> updateWidgetTemplates());
        widgetTemplatesDs.refresh();
    }

    protected void updateWidgetTemplates() {
        ddWidgetTemplateBox.removeAll();

        for (Widget widget : getWidgetTemplates(widgetTemplatesDs.getItems())) {
            PaletteButton widgetBtn = factory.createWidgetButton(widget);
            ddWidgetTemplateBox.add(widgetBtn);
        }
    }

    protected List<Widget> getWidgetTemplates(Collection<WidgetTemplate> items) {
        return items.stream()
                .map(widgetTemplate -> converter.widgetFromJson(widgetTemplate.getWidgetModel()))
                .filter(accessHelper::isWidgetTemplateAllowedCurrentUser)
                .collect(Collectors.toList());
    }

    protected List<? extends Widget> getSketchWidgets() {
        return typeAnalyzer.getWidgetTypesInfo()
                .stream()
                .map(type -> {
                    Class<? extends Widget> typeClass = type.getTypeClass();
                    Widget widget = metadata.create(typeClass);
                    widget.setCaption(type.getName());
                    return widget;
                })
                .collect(Collectors.toList());
    }
}
