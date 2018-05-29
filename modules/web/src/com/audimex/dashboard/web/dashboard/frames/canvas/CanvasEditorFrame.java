/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.frames.canvas;

import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.web.dashboard.events.LayoutRemoveEvent;
import com.audimex.dashboard.web.dashboard.events.OpenWidgetEditorEvent;
import com.audimex.dashboard.web.dashboard.tools.DropLayoutTools;
import com.audimex.dashboard.web.dashboard.tools.VaadinComponentsFactory;
import com.audimex.dashboard.web.dashboard.vaadin_components.layouts.CanvasLayout;
import com.audimex.dashboard.web.dashboard.vaadin_components.layouts.CanvasWidgetLayout;
import com.audimex.dashboard.web.widget.WidgetEdit;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.vaadin.ui.AbstractLayout;
import org.springframework.context.event.EventListener;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

import static com.audimex.dashboard.web.DashboardStyleConstants.AMXD_LAYOUT_SELECTED;
import static com.haulmont.cuba.gui.WindowManager.OpenType.THIS_TAB;

public class CanvasEditorFrame extends CanvasFrame {
    public static final String SCREEN_NAME = "canvasEditorFrame";

    @Inject
    protected VBoxLayout canvas;
    @Inject
    protected DropLayoutTools tools;
    @Named("amdx_VaadinDropComponentsFactory")
    protected VaadinComponentsFactory factory;

    @Override
    public void init(Map<String, Object> params) {
        initLayout(factory, params);

        tools.init(this, vLayout);
        addLayoutClickListener();
    }

    @EventListener
    public void onRemoveLayout(LayoutRemoveEvent event) {
        CanvasLayout source = event.getSource();
        AbstractLayout parent = (AbstractLayout) source.getParent();
        parent.removeComponent(source);
    }

    @EventListener
    public void onOpenWidgetEditor(OpenWidgetEditorEvent event) {
        CanvasWidgetLayout source = event.getSource();
        Widget widget = source.getWidget();

        WidgetEdit editor = (WidgetEdit) openEditor(SCREEN_NAME, widget, THIS_TAB);
        editor.addCloseWithCommitListener(() -> {
            AbstractLayout parent = (AbstractLayout) source.getParent();
            parent.removeComponent(source);

            CanvasWidgetLayout newLayout = factory.createCanvasWidgetLayout(this, editor.getItem());
            tools.addDropHandler(newLayout);
            parent.addComponent(newLayout);
        });
    }

    protected void addLayoutClickListener() {
        vLayout.addLayoutClickListener(e -> {
                    if (e.getClickedComponent() != null) {
                        deselectChildren(vLayout);
                        selectLayout(e.getClickedComponent());
                    }
                }
        );
    }

    protected void deselectChildren(AbstractLayout layout) {
        layout.forEach(child -> {
            if (child instanceof AbstractLayout) {
                child.removeStyleName(AMXD_LAYOUT_SELECTED);
                deselectChildren((AbstractLayout) child);
            }
        });
    }

    protected void selectLayout(com.vaadin.ui.Component component) {
        CanvasLayout layout = (CanvasLayout) getCanvasLayoutParent(component);
        layout.addStyleName(AMXD_LAYOUT_SELECTED);
    }

    protected com.vaadin.ui.Component getCanvasLayoutParent(com.vaadin.ui.Component layout) {
        if (layout instanceof CanvasLayout) {
            return layout;
        } else {
            return getCanvasLayoutParent(layout.getParent());
        }
    }
}
