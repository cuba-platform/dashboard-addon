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

import com.haulmont.addon.dashboard.model.visualmodel.*;
import com.haulmont.addon.dashboard.web.DashboardStyleConstants;
import com.haulmont.addon.dashboard.web.dashboard.events.CanvasLayoutElementClickedEvent;
import com.haulmont.addon.dashboard.web.dashboard.frames.editor.canvas.CanvasFrame;
import com.haulmont.addon.dashboard.web.dashboard.layouts.*;
import com.haulmont.addon.dashboard.web.dashboard.tools.drophandler.CanvasDropListener;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.HBoxLayout;
import com.haulmont.cuba.web.widgets.CubaCssActionsLayout;
import com.vaadin.shared.ui.dnd.EffectAllowed;
import com.vaadin.ui.dnd.DragSourceExtension;
import com.vaadin.ui.dnd.DropTargetExtension;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Component("dashboard_dropComponentsFactory")
public class CanvasDropComponentsFactory extends CanvasUiComponentsFactory {
    @Inject
    protected UiComponents factory;
    @Inject
    protected Events events;
    @Inject
    protected Metadata metadata;
    @Inject
    protected ActionProviderFactory actionProviderFactory;

    @Override
    public CanvasVerticalLayout createCanvasVerticalLayout(VerticalLayout verticalLayout) {
        CanvasVerticalLayout layout = super.createCanvasVerticalLayout(verticalLayout);
//        layout.getDelegate().setSpacing(true);
        initLayout(verticalLayout, layout);
        return layout;
    }

    private void initLayout(DashboardLayout layoutModel, AbstractCanvasLayout layout) {
        layout.addStyleName(DashboardStyleConstants.DASHBOARD_SHADOW_BORDER);
        layout.setDescription(layoutModel.getCaption());
        createBaseLayoutActions(layout, layoutModel);
        addLayoutClickListener(layout);
        initDragExtension(layoutModel, layout);
        initDropExtension(layoutModel, layout);
    }

    private void initDragExtension(DashboardLayout layoutModel, AbstractCanvasLayout layout) {
        DragSourceExtension<com.vaadin.ui.CssLayout> dragSourceExtension = new DragSourceExtension<>(layout.unwrap(com.vaadin.ui.CssLayout.class));
        dragSourceExtension.setEffectAllowed(EffectAllowed.MOVE);
        dragSourceExtension.addDragStartListener(e -> dragSourceExtension.setDragData(layoutModel));
        dragSourceExtension.addDragEndListener(e -> dragSourceExtension.setDragData(null));
    }

    private void initDropExtension(DashboardLayout layoutModel, AbstractCanvasLayout layout) {
        DropTargetExtension<CubaCssActionsLayout> dropTarget = new DropTargetExtension(layout.unwrap(CubaCssActionsLayout.class));
        dropTarget.addDropListener(new CanvasDropListener());
    }

    protected Button createButton(Action action) {
        Button removeButton = factory.create(Button.class);
        removeButton.setAction(action);
        removeButton.addStyleName(DashboardStyleConstants.DASHBOARD_EDIT_BUTTON);
        removeButton.setIcon(action.getIcon());
        removeButton.setCaption("");
        return removeButton;
    }

    @Override
    public CanvasHorizontalLayout createCanvasHorizontalLayout(HorizontalLayout horizontalLayout) {
        CanvasHorizontalLayout layout = super.createCanvasHorizontalLayout(horizontalLayout);
//        layout.getDelegate().setSpacing(true);
        initLayout(horizontalLayout, layout);
        return layout;
    }

    @Override
    public CanvasCssLayout createCssLayout(CssLayout cssLayoutModel) {
        CanvasCssLayout layout = super.createCssLayout(cssLayoutModel);
        initLayout(cssLayoutModel, layout);
        return layout;
    }

    @Override
    public CanvasGridLayout createCanvasGridLayout(GridLayout gridLayout) {
        CanvasGridLayout layout = super.createCanvasGridLayout(gridLayout);
//        layout.getDelegate().setSpacing(true);
        initLayout(gridLayout, layout);
        return layout;
    }

    private void createBaseLayoutActions(CanvasLayout canvasLayout, DashboardLayout layout) {
        HBoxLayout buttonsPanel = canvasLayout.getButtonsPanel();
        buttonsPanel.addStyleName(DashboardStyleConstants.DASHBOARD_LAYOUT_CONTROLS);

        List<Action> actions = actionProviderFactory.getLayoutActions(layout);
        for (Action action : actions) {
            Button button = createButton(action);
            buttonsPanel.add(button);
        }
        Button captionButton = createCaptionButton(layout);
        buttonsPanel.add(captionButton);
    }

    @Override
    public CanvasWidgetLayout createCanvasWidgetLayout(CanvasFrame frame, WidgetLayout widgetLayout) {
        CanvasWidgetLayout layout = super.createCanvasWidgetLayout(frame, widgetLayout);
//        layout.getDelegate().setSpacing(true);
        initLayout(widgetLayout, layout);
        return layout;

    }

    @Override
    public CanvasRootLayout createCanvasRootLayout(RootLayout rootLayout) {
        CanvasRootLayout layout = super.createCanvasRootLayout(rootLayout);
//        layout.getDelegate().setSpacing(true);
        initLayout(rootLayout, layout);
        return layout;
    }

    protected Button createCaptionButton(DashboardLayout layout) {
        Button captionButton = factory.create(Button.class);
        captionButton.addStyleName(DashboardStyleConstants.DASHBOARD_EDIT_BUTTON);
        captionButton.setCaption(layout.getCaption());
        return captionButton;
    }

    protected void addLayoutClickListener(CanvasLayout layout) {
        layout.addLayoutClickListener(e -> {
            CanvasLayout selectedLayout = (CanvasLayout) e.getSource().getParent();
            events.publish(new CanvasLayoutElementClickedEvent(selectedLayout.getUuid(), e.getMouseEventDetails()));

        });
    }
}
