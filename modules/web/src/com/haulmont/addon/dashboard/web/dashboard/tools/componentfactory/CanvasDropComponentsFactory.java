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

package com.haulmont.addon.dashboard.web.dashboard.tools.componentfactory;

import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.model.visualmodel.*;
import com.haulmont.addon.dashboard.web.DashboardIcon;
import com.haulmont.addon.dashboard.web.DashboardStyleConstants;
import com.haulmont.addon.dashboard.web.dashboard.events.CanvasLayoutElementClickedEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.CreateWidgetTemplateEvent;
import com.haulmont.addon.dashboard.web.dashboard.events.model.*;
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
import static com.haulmont.cuba.gui.icons.CubaIcon.*;

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
    public CanvasVerticalLayout createCanvasVerticalLayout(VerticalLayout verticalLayout) {
        CanvasVerticalLayout layout = super.createCanvasVerticalLayout(verticalLayout);
        layout.getDelegate().setSpacing(true);
        layout.setDragMode(CLONE);
        layout.addStyleName(DashboardStyleConstants.DASHBOARD_SHADOW_BORDER);
        layout.setDescription(messages.getMainMessage("verticalLayout"));

        createBaseLayoutAction("verticalLayout", layout, verticalLayout);
        addLayoutClickListener(layout);
        return layout;
    }

    @Override
    public CanvasHorizontalLayout createCanvasHorizontalLayout(HorizontalLayout horizontalLayout) {
        CanvasHorizontalLayout layout = super.createCanvasHorizontalLayout(horizontalLayout);
        layout.getDelegate().setSpacing(true);
        layout.setDragMode(CLONE);
        layout.addStyleName(DashboardStyleConstants.DASHBOARD_SHADOW_BORDER);
        layout.setDescription(messages.getMainMessage("horizontalLayout"));

        createBaseLayoutAction("horizontalLayout", layout, horizontalLayout);
        addLayoutClickListener(layout);
        return layout;
    }

    @Override
    public CanvasCssLayout createCssLayout(CssLayout cssLayoutModel) {
        CanvasCssLayout layout = super.createCssLayout(cssLayoutModel);
        layout.setDragMode(CLONE);
        layout.addStyleName(DashboardStyleConstants.DASHBOARD_SHADOW_BORDER);
        layout.setDescription(messages.getMainMessage("cssLayout"));

        createBaseLayoutAction("cssLayout", layout, cssLayoutModel);
        return layout;
    }

    @Override
    public CanvasGridLayout createCanvasGridLayout(GridLayout gridLayout) {
        CanvasGridLayout layout = super.createCanvasGridLayout(gridLayout);
        layout.getDelegate().setSpacing(true);

        layout.setDragMode(CLONE);
        layout.addStyleName(DashboardStyleConstants.DASHBOARD_SHADOW_BORDER);
        layout.setDescription(messages.getMainMessage("gridLayout"));

        createBaseLayoutAction("gridLayout", layout, gridLayout);
        addLayoutClickListener(layout);
        return layout;
    }

    private void createBaseLayoutAction(String captionKey, CanvasLayout canvasLayout, DashboardLayout layout) {
        Button removeButton = createRemoveButton(layout);
        Button weightButton = createWeightButton(layout);
        Button styleButton = createStyleButton(layout);
        Button expandButton = createExpandButton(layout);
        Button captionButton = createCaptionButton(layout, captionKey);

        HBoxLayout buttonsPanel = canvasLayout.getButtonsPanel();
        buttonsPanel.addStyleName(DashboardStyleConstants.DASHBOARD_LAYOUT_CONTROLS);
        buttonsPanel.add(removeButton);
        buttonsPanel.add(styleButton);
        if (!isParentCssLayout(layout) && !isParentHasExpand(layout)) {
            buttonsPanel.add(weightButton);
        }
        if (isLinearLayout(layout)) {
            buttonsPanel.add(expandButton);
        }
        buttonsPanel.add(captionButton);
    }

    private boolean isLinearLayout(DashboardLayout layout) {
        return layout instanceof HorizontalLayout || layout instanceof VerticalLayout;
    }

    private boolean isParentHasExpand(DashboardLayout layout) {
        DashboardLayout parent = layout.getParent();
        if (isLinearLayout(parent) && parent.getExpand() != null) {
            return parent.getChildren().stream()
                    .anyMatch(e -> e.getId().equals(parent.getExpand()));
        }
        return false;
    }

    @Override
    public CanvasWidgetLayout createCanvasWidgetLayout(CanvasFrame frame, WidgetLayout widgetLayout) {
        CanvasWidgetLayout layout = super.createCanvasWidgetLayout(frame, widgetLayout);
        Widget widget = widgetLayout.getWidget();
        layout.getDelegate().setSpacing(true);
        layout.addStyleName(DashboardStyleConstants.DASHBOARD_SHADOW_BORDER);
        layout.setDescription(widgetLayout.getCaption());

        Button removeButton = createRemoveButton(widgetLayout);
        Button editButton = createEditButton(widgetLayout);
        Button styleButton = createStyleButton(widgetLayout);
        Button weightButton = createWeightButton(widgetLayout);
        Button doTemplateButton = createDoTemplateButton(widget);
        Button captionButton = createCaptionButton(widgetLayout, "widgetLayout");
        captionButton.setCaption(widgetLayout.getCaption());

        HBoxLayout buttonsPanel = layout.getButtonsPanel();
        buttonsPanel.addStyleName(DashboardStyleConstants.DASHBOARD_LAYOUT_CONTROLS);
        buttonsPanel.add(removeButton);
        buttonsPanel.add(editButton);

        buttonsPanel.add(doTemplateButton);
        buttonsPanel.add(styleButton);
        if (!isParentCssLayout(widgetLayout) && !isParentHasExpand(widgetLayout)) {
            buttonsPanel.add(weightButton);
        }
        buttonsPanel.add(captionButton);
        addLayoutClickListener(layout);
        return layout;

    }

    private boolean isParentCssLayout(DashboardLayout layout) {
        return layout.getParent() instanceof CssLayout;
    }

    @Override
    public CanvasRootLayout createCanvasRootLayout(RootLayout rootLayout) {
        CanvasRootLayout layout = super.createCanvasRootLayout(rootLayout);
        layout.getDelegate().setSpacing(true);
        layout.setDragMode(CLONE);
        layout.setDescription(messages.getMainMessage("rootLayout"));

        Button expandButton = createExpandButton(rootLayout);
        Button captionButton = createCaptionButton(rootLayout, "rootLayout");

        HBoxLayout buttonsPanel = layout.getButtonsPanel();
        buttonsPanel.addStyleName(DashboardStyleConstants.DASHBOARD_LAYOUT_CONTROLS);
        buttonsPanel.add(expandButton);

        buttonsPanel.add(captionButton);
        addLayoutClickListener(layout);
        return layout;
    }

    protected Button createEditButton(WidgetLayout layout) {
        Button editButton = factory.createComponent(Button.class);
        editButton.setAction(new BaseAction("editClicked")
                .withHandler(e -> events.publish(new WidgetEditEvent(layout))));
        editButton.addStyleName(DashboardStyleConstants.DASHBOARD_EDIT_BUTTON);
        editButton.setIconFromSet(DashboardIcon.GEAR_ICON);
        editButton.setCaption("");
        return editButton;
    }

    protected Button createRemoveButton(DashboardLayout layout) {
        Button removeButton = factory.createComponent(Button.class);
        removeButton.setAction(new BaseAction("removeClicked")
                .withHandler(e -> events.publish(new WidgetRemovedEvent(layout))));
        removeButton.addStyleName(DashboardStyleConstants.DASHBOARD_EDIT_BUTTON);
        removeButton.setIconFromSet(DashboardIcon.TRASH_ICON);
        removeButton.setCaption("");
        return removeButton;
    }

    protected Button createWeightButton(DashboardLayout layout) {
        Button weightButton = factory.createComponent(Button.class);
        weightButton.setAction(new BaseAction("weightClicked")
                .withHandler(e -> events.publish(new WeightChangedEvent(layout))));
        weightButton.addStyleName(DashboardStyleConstants.DASHBOARD_EDIT_BUTTON);
        weightButton.setIconFromSet(ARROWS);
        weightButton.setCaption("");
        return weightButton;
    }

    protected Button createStyleButton(DashboardLayout layout) {
        Button styleButton = factory.createComponent(Button.class);
        styleButton.setAction(new BaseAction("styleClicked")
                .withHandler(e -> events.publish(new StyleChangedEvent(layout))));
        styleButton.addStyleName(DashboardStyleConstants.DASHBOARD_EDIT_BUTTON);
        styleButton.setIconFromSet(PAINT_BRUSH);
        styleButton.setCaption("");
        return styleButton;
    }

    protected Button createExpandButton(DashboardLayout layout) {
        Button expandButton = factory.createComponent(Button.class);
        expandButton.setAction(new BaseAction("expandClicked")
                .withHandler(e -> events.publish(new ExpandChangedEvent(layout))));
        expandButton.addStyleName(DashboardStyleConstants.DASHBOARD_EDIT_BUTTON);
        expandButton.setIconFromSet(EXPAND);
        expandButton.setCaption("");
        return expandButton;
    }

    protected Button createDoTemplateButton(Widget widget) {
        Button templateBtn = factory.createComponent(Button.class);
        templateBtn.setAction(new BaseAction("doTemplateClicked")
                .withHandler(e -> events.publish(new CreateWidgetTemplateEvent(widget))));
        templateBtn.addStyleName(DashboardStyleConstants.DASHBOARD_EDIT_BUTTON);
        templateBtn.setIconFromSet(DATABASE);
        templateBtn.setCaption("");
        return templateBtn;
    }


    protected Button createCaptionButton(DashboardLayout layout, String messageKey) {
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
