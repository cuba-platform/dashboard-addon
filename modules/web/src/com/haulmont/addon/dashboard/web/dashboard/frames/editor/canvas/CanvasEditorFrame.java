/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas;

import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.model.visual_model.ContainerLayout;
import com.haulmont.addon.dashboard.model.visual_model.DashboardLayout;
import com.haulmont.addon.dashboard.model.visual_model.VerticalLayout;
import com.haulmont.addon.dashboard.model.visual_model.WidgetLayout;
import com.haulmont.addon.dashboard.web.DashboardStyleConstants;
import com.haulmont.addon.dashboard.web.annotation_analyzer.WidgetRepository;
import com.haulmont.addon.dashboard.web.dashboard.events.*;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.weight_dialog.WeightDialog;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasLayout;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasWidgetLayout;
import com.haulmont.addon.dashboard.web.dashboard.tools.DashboardModelConverter;
import com.haulmont.addon.dashboard.web.dashboard.tools.DropLayoutTools;
import com.haulmont.addon.dashboard.web.dashboard.tools.drop_handlers.DropHandlerHelper;
import com.haulmont.addon.dashboard.web.dashboard.events.CanvasLayoutElementClickedEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.WidgetAddedEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.WidgetTreeElementClickedEvent;
import com.haulmont.addon.dashboard.web.widget.WidgetEdit;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.gui.components.BoxLayout;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.addon.dashboard.web.dashboard.events.WidgetRemovedFromCanvasEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.OpenWidgetEditorEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.WeightChangedEvent;
import com.haulmont.cuba.gui.components.Window;
import org.springframework.context.event.EventListener;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.haulmont.addon.dashboard.web.dashboard.datasources.DashboardLayoutUtils.findLayout;
import static com.haulmont.addon.dashboard.web.dashboard.datasources.DashboardLayoutUtils.findParentLayout;
import static com.haulmont.cuba.gui.WindowManager.OpenType.DIALOG;
import static com.haulmont.cuba.gui.WindowManager.OpenType.THIS_TAB;

public class CanvasEditorFrame extends CanvasFrame implements DropHandlerHelper {
    public static final String SCREEN_NAME = "canvasEditorFrame";

    @Inject
    protected VBoxLayout canvas;
    @Named("dropModelConverter")
    protected DashboardModelConverter converter;
    protected DropLayoutTools tools = new DropLayoutTools();
    @Inject
    protected Events events;
    @Inject
    protected WidgetRepository widgetRepository;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    public void updateLayout(Dashboard dashboard) {
        super.updateLayout(dashboard);
        vLayout.addStyleName("amxd-main-shadow-border");
        tools.init(this, converter, vLayout);
    }

    @Override
    protected DashboardModelConverter getConverter() {
        return converter;
    }

    @EventListener
    public void onRemoveLayout(WidgetRemovedFromCanvasEvent event) {
        CanvasLayout source = event.getSource();
        Container parent = (Container) source.getParent();
        if (parent != null) {
            parent.remove(source);
        }
        events.publish(new DashboardRefreshEvent(getDashboardModel()));
    }

    @EventListener
    public void onOpenWidgetEditor(OpenWidgetEditorEvent event) {
        CanvasWidgetLayout source = event.getSource();
        Widget widget = source.getWidget();

        WidgetEdit editor = (WidgetEdit) openEditor(WidgetEdit.SCREEN_NAME, widget, THIS_TAB);
        editor.addCloseWithCommitListener(() -> {
            Container parent = (Container) source.getParent();
            parent.remove(source);

            CanvasWidgetLayout newLayout = converter.getFactory().createCanvasWidgetLayout(this, editor.getItem());
            newLayout.setUuid(source.getUuid());
            tools.addDropHandler(newLayout);
            parent.add(newLayout);
        });
    }

    @EventListener
    public void onWeightChanged(WeightChangedEvent event) {
        CanvasLayout source = event.getSource();

        WeightDialog weightDialog = (WeightDialog) openWindow(WeightDialog.SCREEN_NAME, DIALOG, ParamsMap.of(
                WeightDialog.WEIGHT, source.getWeight()));
        weightDialog.addCloseListener(actionId -> {
            if (Window.COMMIT_ACTION_ID.equals(actionId)) {
                int weight = weightDialog.getWeight();
                source.setWeight(weight);
            }
        });
    }

    protected void selectLayout(CanvasLayout layout, UUID layoutUuid, boolean needSelect) {
        if (layout.getUuid().equals(layoutUuid) && needSelect) {
            layout.addStyleName(DashboardStyleConstants.AMXD_TREE_SELECTED);
        } else {
            layout.removeStyleName(DashboardStyleConstants.AMXD_TREE_SELECTED);
        }

        for (Component child : ((Container) layout.getDelegate()).getOwnComponents()) {
            if (!(child instanceof CanvasLayout)) {
                continue;
            }
            selectLayout((CanvasLayout) child, layoutUuid, needSelect);
        }
    }

    protected CanvasLayout findCanvasLayout(CanvasLayout layout, UUID layoutUuid) {
        if (layout.getUuid().equals(layoutUuid)) {
            return layout;
        } else {
            for (Component child : ((Container) layout.getDelegate()).getOwnComponents()) {
                if (!(child instanceof CanvasLayout)) {
                    continue;
                }
                CanvasLayout tmp = findCanvasLayout((CanvasLayout) child, layoutUuid);
                if (tmp == null) {
                    continue;
                }
                return findCanvasLayout((CanvasLayout) child, layoutUuid);
            }
        }
        return null;
    }

    @EventListener
    public void widgetTreeElementClickedEventListener(WidgetTreeElementClickedEvent event) {
        UUID layoutUuid = event.getSource();
        selectLayout(vLayout, layoutUuid, true);
    }

    @EventListener
    public void canvasLayoutElementClickedEventListener(CanvasLayoutElementClickedEvent event) {
        UUID layoutUuid = event.getSource();
        selectLayout(vLayout, layoutUuid, false);
    }

    @EventListener
    public void widgetAddedToTreeEventListener(WidgetAddedEvent event) {
        tools.addComponent((BoxLayout) findCanvasLayout(vLayout, event.getParentLayoutUuid()).getDelegate(), event.getSource());
    }

    @EventListener
    public void widgetMovedToTreeEventListener(WidgetMovedEvent event) {
        VerticalLayout dashboardModel = getDashboardModel();
        DashboardLayout target = findLayout(dashboardModel, event.getParentLayoutUuid());
        DashboardLayout layout = findLayout(dashboardModel, event.getSource().getId());
        DashboardLayout parent = findParentLayout(dashboardModel, layout);

        parent.getChildren().remove(layout);
        if (target instanceof ContainerLayout) {
            switch (event.getLocation()) {
                case MIDDLE:
                    target.addChild(layout);
                    break;
                case BOTTOM:
                    List<DashboardLayout> newChildren = new ArrayList<>();
                    newChildren.add(layout);
                    newChildren.addAll(parent.getChildren());
                    parent.setChildren(newChildren);
                    break;
                case TOP:
                    newChildren = new ArrayList<>(parent.getChildren());
                    newChildren.add(layout);
                    parent.setChildren(newChildren);
                    break;
            }
        }
        if (target instanceof WidgetLayout) {
            List<DashboardLayout> newChildren = new ArrayList<>();
            for (DashboardLayout childLayout : parent.getChildren()) {
                if (childLayout.getId().equals(target.getId())) {
                    switch (event.getLocation()) {
                        case TOP:
                            newChildren.add(layout);
                            newChildren.add(childLayout);
                            break;
                        case MIDDLE:
                        case BOTTOM:
                            newChildren.add(childLayout);
                            newChildren.add(layout);
                            break;
                    }
                } else {
                    newChildren.add(childLayout);
                }
            }
            parent.setChildren(newChildren);
        }

        events.publish(new DashboardRefreshEvent(dashboardModel, event.getSource().getId()));
    }

    @EventListener
    public void onLayoutRefreshedEvent(DashboardRefreshEvent event) {
        VerticalLayout dashboardModel = (VerticalLayout) event.getSource();
        dashboard.setVisualModel(dashboardModel);
        updateLayout(dashboard);
    }
}
