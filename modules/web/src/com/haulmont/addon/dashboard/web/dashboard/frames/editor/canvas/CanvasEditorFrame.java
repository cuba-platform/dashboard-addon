/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas;

import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.model.dto.LayoutRemoveDto;
import com.haulmont.addon.dashboard.model.visual_model.VerticalLayout;
import com.haulmont.addon.dashboard.web.DashboardStyleConstants;
import com.haulmont.addon.dashboard.web.dashboard.events.*;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.weight_dialog.WeightDialog;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasLayout;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasWidgetLayout;
import com.haulmont.addon.dashboard.web.dashboard.tools.DashboardModelConverter;
import com.haulmont.addon.dashboard.web.dashboard.tools.DropLayoutTools;
import com.haulmont.addon.dashboard.web.dashboard.tools.drop_handlers.DropHandlerHelper;
import com.haulmont.addon.dashboard.web.events.CanvasLayoutElementClickedEvent;
import com.haulmont.addon.dashboard.web.events.WidgetTreeElementClickedEvent;
import com.haulmont.addon.dashboard.web.widget.WidgetEdit;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.addon.dashboard.web.dashboard.events.LayoutRemoveEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.OpenWidgetEditorEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.WeightChangedEvent;
import org.springframework.context.event.EventListener;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

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
    public void onRemoveLayout(LayoutRemoveEvent event) {
        CanvasLayout source = event.getSource();
        Container parent = (Container) source.getParent();
        if (parent != null) {
            parent.remove(source);
        }
        VerticalLayout vl = getDashboardModel();
        events.publish(new LayoutChangedEvent(new LayoutRemoveDto(vl, source.getUuid())));
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
            if ("commit".equals(actionId)) {
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
}
