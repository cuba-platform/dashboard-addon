/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.frames.editor.palette;

import com.haulmont.addon.dashboard.entity.WidgetTemplate;
import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.model.visualmodel.DashboardLayout;
import com.haulmont.addon.dashboard.model.visualmodel.RootLayout;
import com.haulmont.addon.dashboard.web.dashboard.converter.JsonConverter;
import com.haulmont.addon.dashboard.web.dashboard.datasources.DashboardLayoutTreeReadOnlyDs;
import com.haulmont.addon.dashboard.web.dashboard.events.DashboardRefreshEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.DoWidgetTemplateEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.WidgetSelectedEvent;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.DashboardLayoutHolderComponent;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.components.PaletteButton;
import com.haulmont.addon.dashboard.web.dashboard.tools.AccessConstraintsHelper;
import com.haulmont.addon.dashboard.web.dashboard.tools.componentfactory.PaletteComponentsFactory;
import com.haulmont.addon.dashboard.web.dashboard.tools.drophandler.NotDropHandler;
import com.haulmont.addon.dashboard.web.dashboard.tools.drophandler.TreeDropHandler;
import com.haulmont.addon.dashboard.web.repository.WidgetRepository;
import com.haulmont.addon.dnd.components.DDVerticalLayout;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Tree;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.web.toolkit.ui.CubaTree;
import org.springframework.context.event.EventListener;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.haulmont.addon.dashboard.utils.DashboardLayoutUtils.findLayout;
import static com.haulmont.addon.dashboard.utils.DashboardLayoutUtils.findParentLayout;

public class PaletteFrame extends AbstractFrame implements DashboardLayoutHolderComponent {
    public static final String SCREEN_NAME = "dashboard$PaletteFrame";

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
    protected WidgetRepository widgetRepository;
    @Inject
    protected DashboardLayoutTreeReadOnlyDs dashboardLayoutTreeReadOnlyDs;
    @Inject
    protected Tree<DashboardLayout> widgetTree;
    @Inject
    protected Events events;
    @Inject
    protected WindowConfig windowConfig;
    @WindowParam
    protected Dashboard dashboard;

    @Override
    public void init(Map<String, Object> params) {
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
                events.publish(new WidgetSelectedEvent(dashboardLayout.getId(), WidgetSelectedEvent.Target.TREE));
            }
        });
        widgetTree.expandTree();

        CubaTree cubaTree = widgetTree.unwrap(CubaTree.class);
        cubaTree.setDragMode(com.vaadin.ui.Tree.TreeDragMode.NODE);
        cubaTree.setDropHandler(new TreeDropHandler(dashboardLayoutTreeReadOnlyDs));
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
        return widgetRepository.getWidgetTypesInfo()
                .stream()
                .map(type -> {
                    Widget widget = metadata.create(Widget.class);
                    widget.setCaption(type.getName());
                    widget.setBrowseFrameId(type.getBrowseFrameId());
                    widget.setDashboard(dashboard);
                    return widget;
                })
                .collect(Collectors.toList());
    }

    @EventListener
    public void onWidgetSelectedEvent(WidgetSelectedEvent event) {
        if (WidgetSelectedEvent.Target.TREE != event.getTarget()) {
            UUID layoutUuid = event.getSource();
            DashboardLayout layout = dashboardLayoutTreeReadOnlyDs.getVisualModel().findLayout(layoutUuid);
            widgetTree.setSelected(layout);
            widgetTree.expand(layout.getUuid());
        }
    }

    @EventListener
    public void onLayoutRefreshedEvent(DashboardRefreshEvent event) {
        DashboardLayout selected = event.getSelectId() != null ?
                findLayout(dashboardLayoutTreeReadOnlyDs.getVisualModel(), event.getSelectId())
                : widgetTree.getSingleSelected();
        DashboardLayout parent = selected != null ?
                findParentLayout(dashboardLayoutTreeReadOnlyDs.getVisualModel(), selected) : null;
        dashboardLayoutTreeReadOnlyDs.setVisualModel(event.getSource());
        dashboard.setVisualModel((RootLayout) event.getSource());
        dashboardLayoutTreeReadOnlyDs.refresh();
        if (selected != null) {
            DashboardLayout dashboardLayout = dashboardLayoutTreeReadOnlyDs.getItem(selected.getId());
            if (dashboardLayout == null) {
                selected = parent;
                if (selected != null) {
                    events.publish(new WidgetSelectedEvent(selected.getId(), WidgetSelectedEvent.Target.TREE));
                }
            }
        }
        widgetTree.repaint();
    }

    public DashboardLayoutTreeReadOnlyDs getDashboardLayoutTreeReadOnlyDs() {
        return dashboardLayoutTreeReadOnlyDs;
    }
}
