/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.tools.component_factories;

import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.model.visual_model.GridLayout;
import com.audimex.dashboard.model.visual_model.VerticalLayout;
import com.audimex.dashboard.model.visual_model.WidgetLayout;
import com.audimex.dashboard.web.dashboard.events.LayoutRemoveEvent;
import com.audimex.dashboard.web.dashboard.events.OpenWidgetEditorEvent;
import com.audimex.dashboard.web.dashboard.events.WeightChangedEvent;
import com.audimex.dashboard.web.dashboard.frames.canvas.CanvasFrame;
import com.audimex.dashboard.web.dashboard.frames.editor.vaadin_components.PaletteButton;
import com.audimex.dashboard.web.dashboard.layouts.CanvasGridLayout;
import com.audimex.dashboard.web.dashboard.layouts.CanvasHorizontalLayout;
import com.audimex.dashboard.web.dashboard.layouts.CanvasVerticalLayout;
import com.audimex.dashboard.web.dashboard.layouts.CanvasWidgetLayout;
import com.audimex.dashboard.web.dashboard.tools.drop_handlers.NotDropHandler;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.icons.CubaIcon;
import com.haulmont.cuba.web.gui.icons.IconResolver;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import fi.jasoft.dragdroplayouts.DDGridLayout;
import fi.jasoft.dragdroplayouts.DDHorizontalLayout;
import fi.jasoft.dragdroplayouts.DDVerticalLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;

import javax.inject.Inject;

import static com.audimex.dashboard.web.DashboardIcon.*;
import static com.audimex.dashboard.web.DashboardStyleConstants.*;
import static fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode.CLONE;

@org.springframework.stereotype.Component("amdx_VaadinDropComponentsFactory")
public class VaadinDropComponentsFactory extends VaadinUiComponentsFactory {
    @Inject
    protected IconResolver iconResolver;
    @Inject
    protected Events events;
    @Inject
    protected Metadata metadata;
    @Inject
    protected Messages messages;

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
        Button weightButton = createWeightButton();
        weightButton.addClickListener(e -> events.publish(new WeightChangedEvent(layout)));

        HorizontalLayout buttonsPanel = layout.getButtonsPanel();
        buttonsPanel.addStyleName(AMXD_LAYOUT_CONTROLS);
        buttonsPanel.addComponent(removeButton);
        buttonsPanel.addComponent(weightButton);

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
        Button weightButton = createWeightButton();
        weightButton.addClickListener(e -> events.publish(new WeightChangedEvent(layout)));

        HorizontalLayout buttonsPanel = layout.getButtonsPanel();
        buttonsPanel.addStyleName(AMXD_LAYOUT_CONTROLS);
        buttonsPanel.addComponent(removeButton);
        buttonsPanel.addComponent(weightButton);

        DDGridLayout gridLayout = layout.getDelegate();
        gridLayout.setDragMode(CLONE);
        gridLayout.addStyleName(AMXD_LAYOUT_CONTENT);

        return layout;
    }

    @Override
    public CanvasWidgetLayout createCanvasWidgetLayout(CanvasFrame frame, Widget widget) {
        CanvasWidgetLayout layout = super.createCanvasWidgetLayout(frame, widget);
        layout.setDragMode(CLONE);
        layout.addStyleName(AMXD_SHADOW_BORDER);

        Button removeButton = createRemoveButton();
        removeButton.addClickListener(e -> events.publish(new LayoutRemoveEvent(layout)));
        Button editButton = createEditButton();
        editButton.addClickListener(e -> events.publish(new OpenWidgetEditorEvent(layout)));
        Button weightButton = createWeightButton();
        weightButton.addClickListener(e -> events.publish(new WeightChangedEvent(layout)));

        HorizontalLayout buttonsPanel = layout.getButtonsPanel();
        buttonsPanel.addStyleName(AMXD_LAYOUT_CONTROLS);
        buttonsPanel.addComponent(removeButton);
        buttonsPanel.addComponent(editButton);
        buttonsPanel.addComponent(weightButton);

        DDVerticalLayout verticalLayout = layout.getDelegate();
        verticalLayout.setDragMode(CLONE);
        verticalLayout.addStyleName(AMXD_LAYOUT_CONTENT);

        return layout;

    }

    public DDVerticalLayout createNotDroppedVerticalLayout() {
        DDVerticalLayout layout = new DDVerticalLayout();
        layout.setDragMode(LayoutDragMode.CLONE);
        layout.setDropHandler(new NotDropHandler());
        layout.setMargin(new MarginInfo(false, true));
        return layout;
    }

    public PaletteButton createPaletteVerticalLayoutButton() {
        PaletteButton button = new PaletteButton();
        button.setCaption(messages.getMainMessage("verticalLayout"));
        button.setIcon(iconResolver.getIconResource(VERTICAL_LAYOUT_ICON.source()));
        button.setLayout(metadata.create(VerticalLayout.class));
        button.setDescription(messages.getMainMessage("verticalLayout"));
        button.setWidth("100%");
        button.setHeight("50px");
        button.setStyleName(AMXD_DASHBOARD_BUTTON);
        return button;
    }

    public PaletteButton createPaletteHorizontalLayoutButton() {
        PaletteButton button = new PaletteButton();
        button.setCaption(messages.getMainMessage("horizontalLayout"));
        button.setIcon(iconResolver.getIconResource(HORIZONTAL_LAYOUT_ICON.source()));
        button.setLayout(metadata.create(com.audimex.dashboard.model.visual_model.HorizontalLayout.class));
        button.setDescription(messages.getMainMessage("horizontalLayout"));
        button.setWidth("100%");
        button.setHeight("50px");
        button.setStyleName(AMXD_DASHBOARD_BUTTON);
        return button;
    }

    public PaletteButton createPaletteGridLayoutButton() {
        PaletteButton button = new PaletteButton();
        button.setCaption(messages.getMainMessage("gridLayout"));
        button.setIcon(iconResolver.getIconResource(GRID_LAYOUT_ICON.source()));
        button.setLayout(metadata.create(GridLayout.class));
        button.setDescription(messages.getMainMessage("gridLayout"));
        button.setWidth("100%");
        button.setHeight("50px");
        button.setStyleName(AMXD_DASHBOARD_BUTTON);
        return button;
    }

    public PaletteButton createPaletteWidgetButton(Widget widget) {
        WidgetLayout layout = metadata.create(WidgetLayout.class);
        layout.setWidget(widget);

        PaletteButton button = new PaletteButton();
        button.setCaption(widget.getCaption());
        button.setDescription(widget.getDescription());
        button.setLayout(layout);
        button.setWidth("100%");
        button.setHeight("50px");
        button.setStyleName(AMXD_DASHBOARD_BUTTON);
        return button;
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
