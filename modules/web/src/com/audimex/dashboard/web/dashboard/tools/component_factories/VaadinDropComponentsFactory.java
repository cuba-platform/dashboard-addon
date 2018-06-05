/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.tools.component_factories;

import com.audimex.dashboard.model.Dashboard;
import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.web.dashboard.events.LayoutRemoveEvent;
import com.audimex.dashboard.web.dashboard.events.OpenWidgetEditorEvent;
import com.audimex.dashboard.web.dashboard.events.WeightChangedEvent;
import com.audimex.dashboard.web.dashboard.layouts.CanvasGridLayout;
import com.audimex.dashboard.web.dashboard.layouts.CanvasHorizontalLayout;
import com.audimex.dashboard.web.dashboard.layouts.CanvasVerticalLayout;
import com.audimex.dashboard.web.dashboard.layouts.CanvasWidgetLayout;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.icons.CubaIcon;
import com.haulmont.cuba.web.gui.icons.IconResolver;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import fi.jasoft.dragdroplayouts.DDGridLayout;
import fi.jasoft.dragdroplayouts.DDHorizontalLayout;
import fi.jasoft.dragdroplayouts.DDVerticalLayout;

import javax.inject.Inject;

import static com.audimex.dashboard.web.DashboardIcon.GEAR_ICON;
import static com.audimex.dashboard.web.DashboardIcon.TRASH_ICON;
import static com.audimex.dashboard.web.DashboardStyleConstants.*;
import static fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode.CLONE;

@org.springframework.stereotype.Component("amdx_VaadinDropComponentsFactory")
public class VaadinDropComponentsFactory extends VaadinUiComponentsFactory {
    @Inject
    protected IconResolver iconResolver;
    @Inject
    protected Events events;

    @Override
    public CanvasVerticalLayout createCanvasVerticalLayout() {
        CanvasVerticalLayout layout = super.createCanvasVerticalLayout();

        layout.setDragMode(CLONE);
        layout.addStyleName(AMXD_SHADOW_BORDER);

        Button removeButton = createRemoveButton();
        removeButton.addClickListener(e -> events.publish(new LayoutRemoveEvent(layout)));
        Button weightButton = createWeightButton();
        weightButton.addClickListener(e -> events.publish(new WeightChangedEvent(layout)));

        HorizontalLayout buttonsPanel = layout.getButtonsPanel();
        buttonsPanel.addStyleName(AMXD_LAYOUT_CONTROLS);
        buttonsPanel.addComponent(removeButton);
        buttonsPanel.addComponent(weightButton);

        DDVerticalLayout verticalLayout = layout.getDelegate();
        verticalLayout.setDragMode(CLONE);
        verticalLayout.addStyleName(AMXD_LAYOUT_CONTENT);

        return layout;
    }

    @Override
    public CanvasHorizontalLayout createCanvasHorizontalLayout() {
        CanvasHorizontalLayout layout = super.createCanvasHorizontalLayout();
        layout.setDragMode(CLONE);
        layout.addStyleName(AMXD_SHADOW_BORDER);

        Button removeButton = createRemoveButton();
        removeButton.addClickListener(e -> events.publish(new LayoutRemoveEvent(layout)));

        HorizontalLayout buttonsPanel = layout.getButtonsPanel();
        buttonsPanel.addStyleName(AMXD_LAYOUT_CONTROLS);
        buttonsPanel.addComponent(removeButton);

        DDHorizontalLayout horizontalLayout = layout.getDelegate();
        horizontalLayout.setDragMode(CLONE);
        horizontalLayout.addStyleName(AMXD_LAYOUT_CONTENT);

        return layout;
    }

    @Override
    public CanvasGridLayout createCanvasGridLayout(int cols, int rows) {
        CanvasGridLayout layout = super.createCanvasGridLayout(cols, rows);
        layout.setDragMode(CLONE);
        layout.addStyleName(AMXD_SHADOW_BORDER);

        Button removeButton = createRemoveButton();
        removeButton.addClickListener(e -> events.publish(new LayoutRemoveEvent(layout)));

        HorizontalLayout buttonsPanel = layout.getButtonsPanel();
        buttonsPanel.addStyleName(AMXD_LAYOUT_CONTROLS);
        buttonsPanel.addComponent(removeButton);

        DDGridLayout gridLayout = layout.getDelegate();
        gridLayout.setDragMode(CLONE);
        gridLayout.addStyleName(AMXD_LAYOUT_CONTENT);

        return layout;
    }

    @Override
    public CanvasWidgetLayout createCanvasWidgetLayout(Frame targetFrame, Widget widget, Dashboard dashboard) {
        CanvasWidgetLayout layout = super.createCanvasWidgetLayout(targetFrame, widget, dashboard);
        layout.setDragMode(CLONE);
        layout.addStyleName(AMXD_SHADOW_BORDER);

        Button removeButton = createRemoveButton();
        removeButton.addClickListener(e -> events.publish(new LayoutRemoveEvent(layout)));
        Button editButton = createEditButton();
        editButton.addClickListener(e -> events.publish(new OpenWidgetEditorEvent(layout)));

        HorizontalLayout buttonsPanel = layout.getButtonsPanel();
        buttonsPanel.addStyleName(AMXD_LAYOUT_CONTROLS);
        buttonsPanel.addComponent(removeButton);
        buttonsPanel.addComponent(editButton);

        DDVerticalLayout verticalLayout = layout.getDelegate();
        verticalLayout.setDragMode(CLONE);
        verticalLayout.addStyleName(AMXD_LAYOUT_CONTENT);

        return layout;

    }

    protected Button createEditButton() {
        Button editButton = new Button();
        editButton.addStyleName(AMXD_EDIT_BUTTON);
        editButton.setIcon(iconResolver.getIconResource(GEAR_ICON.source()));
        return editButton;
    }

    protected Button createRemoveButton() {
        Button removeButton = new Button();
        removeButton.addStyleName(AMXD_EDIT_BUTTON);
        removeButton.setIcon(iconResolver.getIconResource(TRASH_ICON.source()));
        return removeButton;
    }

    protected Button createWeightButton() {
        Button weightButton = new Button();
        weightButton.addStyleName(AMXD_EDIT_BUTTON);
        weightButton.setIcon(iconResolver.getIconResource(CubaIcon.ARROWS.source()));
        return weightButton;
    }
}
