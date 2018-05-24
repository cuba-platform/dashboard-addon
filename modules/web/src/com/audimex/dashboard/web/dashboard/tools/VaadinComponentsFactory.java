/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.tools;

import com.audimex.dashboard.annotation_analyzer.WidgetTypeAnalyzer;
import com.audimex.dashboard.annotation_analyzer.WidgetTypeInfo;
import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.model.visual_model.WidgetLayout;
import com.audimex.dashboard.web.dashboard.events.LayoutRemoveEvent;
import com.audimex.dashboard.web.dashboard.frames.canvas.CanvasFrame;
import com.audimex.dashboard.web.dashboard.vaadin_components.layouts.CanvasGridLayout;
import com.audimex.dashboard.web.dashboard.vaadin_components.layouts.CanvasHorizontalLayout;
import com.audimex.dashboard.web.dashboard.vaadin_components.layouts.CanvasVerticalLayout;
import com.audimex.dashboard.web.dashboard.vaadin_components.layouts.CanvasWidgetLayout;
import com.audimex.dashboard.web.widget_types.AbstractWidgetBrowse;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.web.gui.icons.IconResolver;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import fi.jasoft.dragdroplayouts.DDVerticalLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;

import javax.inject.Inject;
import java.util.Optional;

import static com.audimex.dashboard.web.DashboardIcon.GEAR_ICON;
import static com.audimex.dashboard.web.DashboardIcon.TRASH_ICON;
import static com.audimex.dashboard.web.DashboardStyleConstants.*;
import static com.audimex.dashboard.web.widget_types.AbstractWidgetBrowse.WIDGET;

@org.springframework.stereotype.Component
public class VaadinComponentsFactory {
    @Inject
    protected WidgetTypeAnalyzer typeAnalyzer;
    @Inject
    protected IconResolver iconResolver;
    @Inject
    protected Messages messages;
    @Inject
    protected Events events;

    public CanvasVerticalLayout createCanvasVerticalLayout() {
        CanvasVerticalLayout layout = new CanvasVerticalLayout();
        layout.setDragMode(LayoutDragMode.CLONE);
        layout.setSizeFull();
        layout.addStyleName(AMXD_SHADOW_BORDER);

        Button removeButton = createRemoveButton();
        removeButton.addClickListener(e -> {
            events.publish(new LayoutRemoveEvent(layout));
        });

        HorizontalLayout buttonsPanel = layout.getButtonsPanel();
        buttonsPanel.addStyleName(AMXD_LAYOUT_CONTROLS);
        buttonsPanel.addComponent(removeButton);

        DDVerticalLayout verticalLayout = layout.getVerticalLayout();
        verticalLayout.setDragMode(fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode.CLONE);
        verticalLayout.setSizeFull();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(true);
        verticalLayout.addStyleName(AMXD_LAYOUT_CONTENT);

        return layout;
    }

    public CanvasHorizontalLayout createCanvasHorizontalLayout() {
        return new CanvasHorizontalLayout();
    }

    public CanvasGridLayout createCanvasGridLayout(int cols, int rows) {
        return new CanvasGridLayout(cols, rows);
    }

    public CanvasWidgetLayout createCanvasWidgetLayout(CanvasFrame targetFrame, WidgetLayout layout) {
        CanvasWidgetLayout component = null;
        Widget widget = layout.getWidget();

        Optional<WidgetTypeInfo> widgetTypeOpt = typeAnalyzer.getWidgetTypesInfo().stream()
                .filter(widgetType -> widget.getClass().equals(widgetType.getTypeClass()))
                .findFirst();

        if (widgetTypeOpt.isPresent()) {
            String frameId = widgetTypeOpt.get().getBrowseFrameId();
            AbstractWidgetBrowse widgetFrame = (AbstractWidgetBrowse) targetFrame.openFrame(null, frameId, ParamsMap.of(WIDGET, widget));
            widgetFrame.setSizeFull();
            widgetFrame.setMargin(true);

            component = new CanvasWidgetLayout();
            component.addComponent(widgetFrame.unwrap(Layout.class));
        }
        return component;
    }

    protected Button createConfigButton() {
        Button configButton = new Button();
        configButton.addStyleName(AMXD_EDIT_BUTTON);
        configButton.setIcon(iconResolver.getIconResource(GEAR_ICON.source()));
        return configButton;
    }

    protected Button createRemoveButton() {
        Button removeButton = new Button();
        removeButton.addStyleName(AMXD_EDIT_BUTTON);
        removeButton.setIcon(iconResolver.getIconResource(TRASH_ICON.source()));
        return removeButton;
    }
}
