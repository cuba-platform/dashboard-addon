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
import com.haulmont.addon.dnd.web.gui.components.WebDragAndDropWrapper;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.HBoxLayout;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.icons.IconResolver;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

import static com.haulmont.addon.dnd.components.DragAndDropWrapper.DragStartMode.WRAPPER;
import static com.haulmont.addon.dnd.components.enums.LayoutDragMode.CLONE;

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
    @Inject
    protected ActionProviderFactory actionProviderFactory;

    @Override
    public CanvasVerticalLayout createCanvasVerticalLayout(VerticalLayout verticalLayout) {
        CanvasVerticalLayout layout = super.createCanvasVerticalLayout(verticalLayout);
        layout.getDelegate().setSpacing(true);
        initDropLayout(verticalLayout, layout);
        return layout;
    }

    private void initDropLayout(DashboardLayout layoutModel, AbstractCanvasLayout layout) {
        if (layout instanceof CanvasResponsiveLayout) {
            ((WebDragAndDropWrapper) layout.getDelegate()).setDragStartMode(WRAPPER);
        } else {
            layout.setDragMode(CLONE);
        }
        layout.addStyleName(DashboardStyleConstants.DASHBOARD_SHADOW_BORDER);
        layout.setDescription(layoutModel.getCaption());
        createBaseLayoutActions(layout, layoutModel);
        addLayoutClickListener(layout);
    }

    protected Button createButton(Action action) {
        Button removeButton = factory.createComponent(Button.class);
        removeButton.setAction(action);
        removeButton.addStyleName(DashboardStyleConstants.DASHBOARD_EDIT_BUTTON);
        removeButton.setIcon(action.getIcon());
        removeButton.setCaption("");
        return removeButton;
    }

    @Override
    public CanvasHorizontalLayout createCanvasHorizontalLayout(HorizontalLayout horizontalLayout) {
        CanvasHorizontalLayout layout = super.createCanvasHorizontalLayout(horizontalLayout);
        layout.getDelegate().setSpacing(true);
        initDropLayout(horizontalLayout, layout);
        return layout;
    }

    @Override
    public CanvasCssLayout createCssLayout(CssLayout cssLayoutModel) {
        CanvasCssLayout layout = super.createCssLayout(cssLayoutModel);
        initDropLayout(cssLayoutModel, layout);
        return layout;
    }

    @Override
    public CanvasGridLayout createCanvasGridLayout(GridLayout gridLayout) {
        CanvasGridLayout layout = super.createCanvasGridLayout(gridLayout);
        layout.getDelegate().setSpacing(true);
        initDropLayout(gridLayout, layout);
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
        layout.getDelegate().setSpacing(true);
        initDropLayout(widgetLayout, layout);
        return layout;

    }

    @Override
    public CanvasRootLayout createCanvasRootLayout(RootLayout rootLayout) {
        CanvasRootLayout layout = super.createCanvasRootLayout(rootLayout);
        layout.getDelegate().setSpacing(true);
        initDropLayout(rootLayout, layout);
        return layout;
    }

    protected Button createCaptionButton(DashboardLayout layout) {
        Button captionButton = factory.createComponent(Button.class);
        captionButton.addStyleName(DashboardStyleConstants.DASHBOARD_EDIT_BUTTON);
        captionButton.setCaption(layout.getCaption());
        return captionButton;
    }

    protected void addLayoutClickListener(CanvasLayout layout) {
        if (!(layout instanceof CanvasResponsiveLayout)) {
            layout.addLayoutClickListener(e -> {
                CanvasLayout selectedLayout = (CanvasLayout) e.getSource().getParent();
                events.publish(new CanvasLayoutElementClickedEvent(selectedLayout.getUuid(), e.getMouseEventDetails()));

            });
        }
    }

    @Override
    public CanvasResponsiveLayout createCanvasResponsiveLayout(ResponsiveLayout rootLayout) {
        CanvasResponsiveLayout layout = super.createCanvasResponsiveLayout(rootLayout);
        //layout.getDelegate().getComponent().setSpacing(true);
        initDropLayout(rootLayout, layout);
        return layout;
    }
}
