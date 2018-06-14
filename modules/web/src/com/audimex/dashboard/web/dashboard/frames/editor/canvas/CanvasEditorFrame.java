/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.frames.editor.canvas;

import com.audimex.dashboard.model.Dashboard;
import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.web.dashboard.events.LayoutRemoveEvent;
import com.audimex.dashboard.web.dashboard.events.OpenWidgetEditorEvent;
import com.audimex.dashboard.web.dashboard.events.WeightChangedEvent;
import com.audimex.dashboard.web.dashboard.frames.editor.weight_dialog.WeightDialog;
import com.audimex.dashboard.web.dashboard.layouts.CanvasLayout;
import com.audimex.dashboard.web.dashboard.layouts.CanvasWidgetLayout;
import com.audimex.dashboard.web.dashboard.tools.DashboardModelConverter;
import com.audimex.dashboard.web.dashboard.tools.DropLayoutTools;
import com.audimex.dashboard.web.dashboard.tools.drop_handlers.DropHandlerHelper;
import com.audimex.dashboard.web.widget.WidgetEdit;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.VBoxLayout;
import org.springframework.context.event.EventListener;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

import static com.haulmont.cuba.gui.WindowManager.OpenType.DIALOG;
import static com.haulmont.cuba.gui.WindowManager.OpenType.THIS_TAB;

public class CanvasEditorFrame extends CanvasFrame implements DropHandlerHelper {
    public static final String SCREEN_NAME = "canvasEditorFrame";

    @Inject
    protected VBoxLayout canvas;
    @Named("dropModelConverter")
    protected DashboardModelConverter converter;

    protected DropLayoutTools tools = new DropLayoutTools();

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
}
