/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.frames.editor.palette;

import com.haulmont.addon.dashboard.annotation_analyzer.WidgetTypeAnalyzer;
import com.haulmont.addon.dashboard.converter.JsonConverter;
import com.haulmont.addon.dashboard.entity.WidgetTemplate;
import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.model.dto.LayoutRemoveDto;
import com.haulmont.addon.dashboard.model.visual_model.DashboardLayout;
import com.haulmont.addon.dashboard.model.visual_model.VerticalLayout;
import com.haulmont.addon.dashboard.web.dashboard.datasources.DashboardLayoutTreeReadOnlyDs;
import com.haulmont.addon.dashboard.web.dashboard.events.DoWidgetTemplateEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.LayoutChangedEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.LayoutRemoveEvent;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.vaadin_components.PaletteButton;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasLayout;
import com.haulmont.addon.dashboard.web.dashboard.tools.AccessConstraintsHelper;
import com.haulmont.addon.dashboard.web.dashboard.tools.component_factories.PaletteComponentsFactory;
import com.haulmont.addon.dashboard.web.dashboard.tools.drop_handlers.NotDropHandler;
import com.haulmont.addon.dashboard.web.events.CanvasLayoutElementClickedEvent;
import com.haulmont.addon.dashboard.web.events.WidgetEntitiesSelectedEvent;
import com.haulmont.addon.dashboard.web.events.WidgetTreeElementClickedEvent;
import com.haulmont.addon.dnd.components.DDVerticalLayout;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Tree;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.web.gui.components.WebTree;
import com.haulmont.cuba.web.toolkit.ui.CubaTree;
import org.springframework.context.event.EventListener;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.haulmont.addon.dashboard.web.dashboard.datasources.DashboardLayoutUtils.findLayout;
import static com.haulmont.addon.dashboard.web.dashboard.datasources.DashboardLayoutUtils.findParentLayout;

public class PaletteFrame extends AbstractFrame {
    public static final String SCREEN_NAME = "paletteFrame";

    private Component.MouseEventDetails mouseEventDetails;

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
    @Inject
    protected DashboardLayoutTreeReadOnlyDs dashboardLayoutTreeReadOnlyDs;
    @Inject
    protected Tree<DashboardLayout> widgetTree;
    @Inject
    protected Events events;

    @Override
    public void init(Map<String, Object> params) {
        Dashboard dashboard = (Dashboard) params.get(CanvasFrame.DASHBOARD);
        dashboardLayoutTreeReadOnlyDs.setVisualModel(dashboard.getVisualModel());
        initWidgetBox();
        initLayoutBox();
        initWidgetTemplateBox();
        initWidgetTreeBox();
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

    protected void initWidgetTreeBox() {
        dashboardLayoutTreeReadOnlyDs.refresh();
        dashboardLayoutTreeReadOnlyDs.addItemChangeListener(e -> {
            DashboardLayout dashboardLayout = widgetTree.getSingleSelected();
            if (dashboardLayout != null) {
                events.publish(new WidgetTreeElementClickedEvent(dashboardLayout.getId()));
            }
        });
        widgetTree.expandTree();

        /*CubaTree cubaTree = widgetTree.unwrap(CubaTree.class);
        cubaTree.setDragMode(com.vaadin.ui.Tree.TreeDragMode.NODE);
        cubaTree.setDropHandler();*/
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

    @EventListener
    public void onLayoutChangedEvent(LayoutChangedEvent event) {
        LayoutRemoveDto source = event.getSource();
        DashboardLayout selected = widgetTree.getSingleSelected();
        if (selected != null) {
            if (selected.getId().equals(source.getRemovedUuid())) {
                selected = findParentLayout(dashboardLayoutTreeReadOnlyDs.getVisualModel(), findLayout(dashboardLayoutTreeReadOnlyDs.getVisualModel(), source.getRemovedUuid()));
            }

        }
        dashboardLayoutTreeReadOnlyDs.setVisualModel(source.getDashboardLayout());
        dashboardLayoutTreeReadOnlyDs.refresh();
        if (selected != null) {
            widgetTree.setSelected(selected);
            widgetTree.expand(selected.getUuid());
        }
        widgetTree.repaint();

    }

    @EventListener
    public void canvasLayoutElementClickedEventListener(CanvasLayoutElementClickedEvent event) {
        UUID layoutUuid = event.getSource();
        Component.MouseEventDetails md = event.getMouseEventDetails();//TODO: refactor, problem with addLayoutClickListener in every layout prod a lot of events, in root can't get clicked comp
        if (mouseEventDetails == null) {
            mouseEventDetails = md;
        } else {
            if (mouseEventDetails.getClientX() == md.getClientX() && mouseEventDetails.getClientY() == md.getClientY()) {
                return;
            } else {
                mouseEventDetails = md;
            }
        }

        widgetTree.setSelected(dashboardLayoutTreeReadOnlyDs.getItem(layoutUuid));
        widgetTree.expand(layoutUuid);
    }
}
