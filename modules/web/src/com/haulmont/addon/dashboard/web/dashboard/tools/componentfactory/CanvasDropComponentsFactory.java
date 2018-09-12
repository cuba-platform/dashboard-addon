/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.tools.componentfactory;

import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.web.DashboardIcon;
import com.haulmont.addon.dashboard.web.DashboardStyleConstants;
import com.haulmont.addon.dashboard.web.dashboard.events.CanvasLayoutElementClickedEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.DoWidgetTemplateEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.canvas.WeightChangedEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.canvas.WidgetRemovedEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.widget.WidgetEditEvent;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame;
import com.haulmont.addon.dashboard.web.dashboard.layouts.*;
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

import static com.haulmont.addon.dnd.components.enums.LayoutDragMode.CLONE;
import static com.haulmont.cuba.gui.icons.CubaIcon.ARROWS;
import static com.haulmont.cuba.gui.icons.CubaIcon.DATABASE;

@Component("dashboard_dropComponentsFactory")
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
        layout.addStyleName(DashboardStyleConstants.DASHBOARD_SHADOW_BORDER);
        layout.setDescription(messages.getMainMessage("verticalLayout"));

        Button removeButton = createRemoveButton(layout);
        Button weightButton = createWeightButton(layout);
        Button captionButton = createCaptionButton(layout, "verticalLayout");

        HBoxLayout buttonsPanel = layout.getButtonsPanel();
        buttonsPanel.addStyleName(DashboardStyleConstants.DASHBOARD_LAYOUT_CONTROLS);
        buttonsPanel.add(removeButton);
        buttonsPanel.add(weightButton);
        buttonsPanel.add(captionButton);
        addLayoutClickListener(layout);
        return layout;
    }

    @Override
    public CanvasHorizontalLayout createCanvasHorizontalLayout() {
        CanvasHorizontalLayout layout = super.createCanvasHorizontalLayout();
        layout.getDelegate().setMargin(true);
        layout.setDragMode(CLONE);
        layout.addStyleName(DashboardStyleConstants.DASHBOARD_SHADOW_BORDER);
        layout.setDescription(messages.getMainMessage("horizontalLayout"));

        Button removeButton = createRemoveButton(layout);
        Button weightButton = createWeightButton(layout);
        Button captionButton = createCaptionButton(layout, "horizontalLayout");

        HBoxLayout buttonsPanel = layout.getButtonsPanel();
        buttonsPanel.addStyleName(DashboardStyleConstants.DASHBOARD_LAYOUT_CONTROLS);
        buttonsPanel.add(removeButton);
        buttonsPanel.add(weightButton);
        buttonsPanel.add(captionButton);
        addLayoutClickListener(layout);
        return layout;
    }

    @Override
    public CanvasGridLayout createCanvasGridLayout(int cols, int rows) {
        CanvasGridLayout layout = super.createCanvasGridLayout(cols, rows);
        layout.getDelegate().setMargin(true);

        layout.setDragMode(CLONE);
        layout.addStyleName(DashboardStyleConstants.DASHBOARD_SHADOW_BORDER);
        layout.setDescription(messages.getMainMessage("gridLayout"));

        Button removeButton = createRemoveButton(layout);
        Button weightButton = createWeightButton(layout);
        Button captionButton = createCaptionButton(layout, "gridLayout");

        HBoxLayout buttonsPanel = layout.getButtonsPanel();
        buttonsPanel.addStyleName(DashboardStyleConstants.DASHBOARD_LAYOUT_CONTROLS);
        buttonsPanel.add(removeButton);
        buttonsPanel.add(weightButton);
        buttonsPanel.add(captionButton);
        addLayoutClickListener(layout);
        return layout;
    }

    @Override
    public CanvasWidgetLayout createCanvasWidgetLayout(CanvasFrame frame, Widget widget) {
        CanvasWidgetLayout layout = super.createCanvasWidgetLayout(frame, widget);
        layout.getDelegate().setMargin(true);
        layout.setDragMode(CLONE);
        layout.addStyleName(DashboardStyleConstants.DASHBOARD_SHADOW_BORDER);
        layout.setDescription(widget.getCaption());

        Button removeButton = createRemoveButton(layout);
        Button editButton = createEditButton(layout);
        Button weightButton = createWeightButton(layout);
        Button doTemplateButton = createDoTemplateButton(widget);
        Button captionButton = createCaptionButton(layout, "widgetLayout");
        captionButton.setCaption(widget.getCaption());

        HBoxLayout buttonsPanel = layout.getButtonsPanel();
        buttonsPanel.addStyleName(DashboardStyleConstants.DASHBOARD_LAYOUT_CONTROLS);
        buttonsPanel.add(removeButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(weightButton);
        buttonsPanel.add(doTemplateButton);
        buttonsPanel.add(captionButton);
        addLayoutClickListener(layout);
        return layout;

    }

    protected Button createEditButton(CanvasWidgetLayout layout) {
        Button editButton = factory.createComponent(Button.class);
        editButton.setAction(new BaseAction("editClicked")
                .withHandler(e -> events.publish(new WidgetEditEvent(layout.getWidget()))));
        editButton.addStyleName(DashboardStyleConstants.DASHBOARD_EDIT_BUTTON);
        editButton.setIconFromSet(DashboardIcon.GEAR_ICON);
        editButton.setCaption("");
        return editButton;
    }

    protected Button createRemoveButton(CanvasLayout layout) {
        Button removeButton = factory.createComponent(Button.class);
        removeButton.setAction(new BaseAction("removeClicked")
                .withHandler(e -> events.publish(new WidgetRemovedEvent(layout))));
        removeButton.addStyleName(DashboardStyleConstants.DASHBOARD_EDIT_BUTTON);
        removeButton.setIconFromSet(DashboardIcon.TRASH_ICON);
        removeButton.setCaption("");
        return removeButton;
    }

    protected Button createWeightButton(CanvasLayout layout) {
        Button weightButton = factory.createComponent(Button.class);
        weightButton.setAction(new BaseAction("weightClicked")
                .withHandler(e -> events.publish(new WeightChangedEvent(layout))));
        weightButton.addStyleName(DashboardStyleConstants.DASHBOARD_EDIT_BUTTON);
        weightButton.setIconFromSet(ARROWS);
        weightButton.setCaption("");
        return weightButton;
    }

    protected Button createDoTemplateButton(Widget widget) {
        Button templateBtn = factory.createComponent(Button.class);
        templateBtn.setAction(new BaseAction("doTemplateClicked")
                .withHandler(e -> events.publish(new DoWidgetTemplateEvent(widget))));
        templateBtn.addStyleName(DashboardStyleConstants.DASHBOARD_EDIT_BUTTON);
        templateBtn.setIconFromSet(DATABASE);
        templateBtn.setCaption("");
        return templateBtn;
    }


    protected Button createCaptionButton(CanvasLayout layout, String messageKey) {
        Button captionButton = factory.createComponent(Button.class);
        captionButton.addStyleName(DashboardStyleConstants.DASHBOARD_EDIT_BUTTON);
        captionButton.setCaption(messages.getMainMessage(messageKey));
        return captionButton;
    }

    protected void addLayoutClickListener(CanvasLayout layout) {
        layout.addLayoutClickListener(e -> {
            CanvasLayout selectedLayout = (CanvasLayout) e.getSource().getParent();
            //events.publish(new WidgetSelectedEvent(selectedLayout.getUuid(), WidgetSelectedEvent.Target.CANVAS));
            events.publish(new CanvasLayoutElementClickedEvent(selectedLayout.getUuid(), e.getMouseEventDetails()));

        });
    }
}
