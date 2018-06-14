/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.tools.component_factories;

import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.web.dashboard.events.LayoutRemoveEvent;
import com.audimex.dashboard.web.dashboard.events.OpenWidgetEditorEvent;
import com.audimex.dashboard.web.dashboard.events.WeightChangedEvent;
import com.audimex.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame;
import com.audimex.dashboard.web.dashboard.layouts.*;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.HBoxLayout;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.icons.IconResolver;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static com.audimex.dashboard.web.DashboardIcon.GEAR_ICON;
import static com.audimex.dashboard.web.DashboardIcon.TRASH_ICON;
import static com.audimex.dashboard.web.DashboardStyleConstants.*;
import static com.haulmont.addon.dnd.components.enums.LayoutDragMode.CLONE;
import static com.haulmont.cuba.gui.icons.CubaIcon.ARROWS;

@Component("amdx_VaadinDropComponentsFactory")
public class CanvasDropComponentsFactory extends CanvasUiComponentsFactory {
    @Inject
    protected ComponentsFactory factory;
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
        layout.getDelegate().setMargin(true);
        layout.setDragMode(CLONE);
        layout.addStyleName(AMXD_SHADOW_BORDER);

        Button removeButton = createRemoveButton(layout);
        Button weightButton = createWeightButton(layout);

        HBoxLayout buttonsPanel = layout.getButtonsPanel();
        buttonsPanel.addStyleName(AMXD_LAYOUT_CONTROLS);
        buttonsPanel.add(removeButton);
        buttonsPanel.add(weightButton);

        return layout;
    }

    @Override
    public CanvasHorizontalLayout createCanvasHorizontalLayout() {
        CanvasHorizontalLayout layout = super.createCanvasHorizontalLayout();
        layout.getDelegate().setMargin(true);
        layout.setDragMode(CLONE);
        layout.addStyleName(AMXD_SHADOW_BORDER);

        Button removeButton = createRemoveButton(layout);
        Button weightButton = createWeightButton(layout);

        HBoxLayout buttonsPanel = layout.getButtonsPanel();
        buttonsPanel.addStyleName(AMXD_LAYOUT_CONTROLS);
        buttonsPanel.add(removeButton);
        buttonsPanel.add(weightButton);

        return layout;
    }

    @Override
    public CanvasGridLayout createCanvasGridLayout(int cols, int rows) {
        CanvasGridLayout layout = super.createCanvasGridLayout(cols, rows);
        layout.getDelegate().setMargin(true);

        layout.setDragMode(CLONE);
        layout.addStyleName(AMXD_SHADOW_BORDER);

        Button removeButton = createRemoveButton(layout);
        Button weightButton = createWeightButton(layout);

        HBoxLayout buttonsPanel = layout.getButtonsPanel();
        buttonsPanel.addStyleName(AMXD_LAYOUT_CONTROLS);
        buttonsPanel.add(removeButton);
        buttonsPanel.add(weightButton);

        return layout;
    }

    @Override
    public CanvasWidgetLayout createCanvasWidgetLayout(CanvasFrame frame, Widget widget) {
        CanvasWidgetLayout layout = super.createCanvasWidgetLayout(frame, widget);
        layout.getDelegate().setMargin(true);
        layout.setDragMode(CLONE);
        layout.addStyleName(AMXD_SHADOW_BORDER);

        Button removeButton = createRemoveButton(layout);
        Button editButton = createEditButton(layout);
        Button weightButton = createWeightButton(layout);

        HBoxLayout buttonsPanel = layout.getButtonsPanel();
        buttonsPanel.addStyleName(AMXD_LAYOUT_CONTROLS);
        buttonsPanel.add(removeButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(weightButton);

        return layout;

    }

    protected Button createEditButton(CanvasWidgetLayout layout) {
        Button editButton = factory.createComponent(Button.class);
        editButton.setAction(new BaseAction("editClicked")
                .withHandler(e -> events.publish(new OpenWidgetEditorEvent(layout))));
        editButton.addStyleName(AMXD_EDIT_BUTTON);
        editButton.setIconFromSet(GEAR_ICON);
        editButton.setCaption("");
        return editButton;
    }

    protected Button createRemoveButton(CanvasLayout layout) {
        Button removeButton = factory.createComponent(Button.class);
        removeButton.setAction(new BaseAction("removeClicked")
                .withHandler(e -> events.publish(new LayoutRemoveEvent(layout))));
        removeButton.addStyleName(AMXD_EDIT_BUTTON);
        removeButton.setIconFromSet(TRASH_ICON);
        removeButton.setCaption("");
        return removeButton;
    }

    protected Button createWeightButton(CanvasLayout layout) {
        Button weightButton = factory.createComponent(Button.class);
        weightButton.setAction(new BaseAction("weightClicked")
                .withHandler(e -> events.publish(new WeightChangedEvent(layout))));
        weightButton.addStyleName(AMXD_EDIT_BUTTON);
        weightButton.setIconFromSet(ARROWS);
        weightButton.setCaption("");
        return weightButton;
    }
}
