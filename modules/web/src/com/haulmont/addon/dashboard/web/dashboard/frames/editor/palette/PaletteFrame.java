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

package com.haulmont.addon.dashboard.web.dashboard.frames.editor.palette;

import com.haulmont.addon.dashboard.entity.WidgetTemplate;
import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.model.visualmodel.DashboardLayout;
import com.haulmont.addon.dashboard.model.visualmodel.RootLayout;
import com.haulmont.addon.dashboard.web.DashboardStyleConstants;
import com.haulmont.addon.dashboard.web.dashboard.converter.JsonConverter;
import com.haulmont.addon.dashboard.web.dashboard.datasources.DashboardLayoutTreeReadOnlyDs;
import com.haulmont.addon.dashboard.web.dashboard.events.CreateWidgetTemplateEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.DashboardRefreshEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.WidgetSelectedEvent;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.DashboardLayoutHolderComponent;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.components.PaletteButton;
import com.haulmont.addon.dashboard.web.dashboard.tools.componentfactory.ActionProviderFactory;
import com.haulmont.addon.dashboard.web.dashboard.tools.componentfactory.PaletteComponentsFactory;
import com.haulmont.addon.dashboard.web.dashboard.tools.drophandler.TreeDropListener;
import com.haulmont.addon.dashboard.web.repository.WidgetRepository;
import com.haulmont.addon.dashboard.web.widgettemplate.WidgetTemplateEdit;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.BoxLayout;
import com.haulmont.cuba.gui.components.Tree;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;
import com.haulmont.cuba.web.widgets.CubaTree;
import com.vaadin.shared.ui.dnd.EffectAllowed;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.ui.components.grid.TreeGridDragSource;
import com.vaadin.ui.components.grid.TreeGridDropTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.haulmont.addon.dashboard.utils.DashboardLayoutUtils.findLayout;
import static com.haulmont.addon.dashboard.utils.DashboardLayoutUtils.findParentLayout;

@UiController("dashboard$PaletteFrame")
@UiDescriptor("palette-frame.xml")
public class PaletteFrame extends AbstractFrame implements DashboardLayoutHolderComponent {
    public static final String SCREEN_NAME = "dashboard$PaletteFrame";

    private static Logger log = LoggerFactory.getLogger(PaletteFrame.class);

    @Inject
    protected BoxLayout ddWidgetBox;
    @Inject
    protected BoxLayout ddLayoutBox;
    @Inject
    protected BoxLayout ddWidgetTemplateBox;
    @Inject
    protected CollectionDatasource<WidgetTemplate, UUID> widgetTemplatesDs;
    @Inject
    protected Metadata metadata;
    @Inject
    protected PaletteComponentsFactory factory;
    @Inject
    protected JsonConverter converter;
    @Inject
    protected WidgetRepository widgetRepository;
    @Inject
    protected DashboardLayoutTreeReadOnlyDs dashboardLayoutTreeReadOnlyDs;
    @Inject
    protected Tree<DashboardLayout> widgetTree;
    @Inject
    protected Events events;
    @WindowParam
    protected Dashboard dashboard;
    @Inject
    protected ActionProviderFactory actionProviderFactory;

    @Override
    public void init(Map<String, Object> params) {
        dashboardLayoutTreeReadOnlyDs.setVisualModel(dashboard.getVisualModel());
        initWidgetBox();
        initLayoutBox();
        initWidgetTemplateBox();
        initWidgetTreeBox();
    }

    @EventListener
    public void onCreateWidgetTemplate(CreateWidgetTemplateEvent event) {
        Widget widget = event.getSource();
        WidgetTemplate widgetTemplate = metadata.create(WidgetTemplate.class);
        widgetTemplate.setWidgetModel(converter.widgetToJson(widget));
        //TODO remove deprecaded code
        WidgetTemplateEdit widgetEditor = (WidgetTemplateEdit) openEditor(
                "dashboard$WidgetTemplate.edit", widgetTemplate, WindowManager.OpenType.DIALOG);
        widgetEditor.addAfterCloseListener(e -> widgetTemplatesDs.refresh());

    }

    protected void initWidgetBox() {
        for (Widget widget : getSketchWidgets()) {
            PaletteButton widgetBtn = factory.createWidgetButton(widget);
            ddWidgetBox.add(widgetBtn);
        }
    }

    protected void initLayoutBox() {
        ddLayoutBox.add(factory.createVerticalLayoutButton());
        ddLayoutBox.add(factory.createHorizontalLayoutButton());
        ddLayoutBox.add(factory.createGridLayoutButton());
        ddLayoutBox.add(factory.createCssLayoutButton());

    }

    protected void initWidgetTreeBox() {
        dashboardLayoutTreeReadOnlyDs.refresh();
        dashboardLayoutTreeReadOnlyDs.addItemChangeListener(e -> {
            DashboardLayout dashboardLayout = widgetTree.getSingleSelected();
            if (dashboardLayout != null) {
                events.publish(new WidgetSelectedEvent(dashboardLayout.getId(), WidgetSelectedEvent.Target.TREE));
            }
            createActions(widgetTree, dashboardLayout);
        });
        widgetTree.expandTree();
        widgetTree.setStyleName(DashboardStyleConstants.DASHBOARD_TREE);

        TreeGridDragSource<DashboardLayout> dropSource = new TreeGridDragSource<DashboardLayout>(widgetTree.unwrap(CubaTree.class).getCompositionRoot());
        dropSource.setEffectAllowed(EffectAllowed.MOVE);
        dropSource.addGridDragStartListener(e -> {
            DashboardLayout containerLayout = e.getDraggedItems().get(0);
            dropSource.setDragData(containerLayout);
        });
        dropSource.addGridDragEndListener(e -> {
            dropSource.setDragData(null);
        });
        TreeGridDropTarget<DashboardLayout> dropTarget = new TreeGridDropTarget<DashboardLayout>(widgetTree.unwrap(CubaTree.class).getCompositionRoot(), DropMode.ON_TOP_OR_BETWEEN);
        dropTarget.addTreeGridDropListener(new TreeDropListener());

    }

    private void createActions(Tree<DashboardLayout> widgetTree, DashboardLayout layout) {
        widgetTree.removeAllActions();
        List<Action> actions = actionProviderFactory.getLayoutActions(layout);
        for (Action action : actions) {
            widgetTree.addAction(action);
        }
    }

    protected void initWidgetTemplateBox() {
        widgetTemplatesDs.addCollectionChangeListener(e -> updateWidgetTemplates());
        widgetTemplatesDs.refresh();
    }

    protected void updateWidgetTemplates() {
        ddWidgetTemplateBox.removeAll();
        Collection<WidgetTemplate> templates = widgetTemplatesDs.getItems().stream()
                .sorted(Comparator.comparing(WidgetTemplate::getName))
                .collect(Collectors.toList());
        for (WidgetTemplate wt : templates) {
            try {
                PaletteButton widgetBtn = factory.createWidgetTemplateButton(wt);
                ddWidgetTemplateBox.add(widgetBtn);
            } catch (Exception e) {
                log.error(String.format("Unable to create widget template %s <%s>. Cause: %s", wt.getName(), wt.getId(), e.getMessage()), e);
            }
        }
    }

    protected List<? extends Widget> getSketchWidgets() {
        return widgetRepository.getWidgetTypesInfo()
                .stream()
                .map(type -> {
                    Widget widget = metadata.create(Widget.class);
                    widget.setName(type.getName());
                    widget.setFrameId(type.getFrameId());
                    widget.setDashboard(dashboard);
                    return widget;
                })
                .sorted(Comparator.comparing(Widget::getName))
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
