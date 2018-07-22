/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.tools.component_factories;

import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.model.visual_model.GridLayout;
import com.audimex.dashboard.model.visual_model.HorizontalLayout;
import com.audimex.dashboard.model.visual_model.VerticalLayout;
import com.audimex.dashboard.model.visual_model.WidgetLayout;
import com.audimex.dashboard.web.dashboard.frames.editor.vaadin_components.PaletteButton;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static com.audimex.dashboard.web.DashboardIcon.GRID_LAYOUT_ICON;
import static com.audimex.dashboard.web.DashboardIcon.HORIZONTAL_LAYOUT_ICON;
import static com.audimex.dashboard.web.DashboardIcon.VERTICAL_LAYOUT_ICON;
import static com.audimex.dashboard.web.DashboardStyleConstants.AMXD_DASHBOARD_BUTTON;

@Component
public class PaletteComponentsFactoryImpl implements PaletteComponentsFactory {
    @Inject
    protected ComponentsFactory factory;
    @Inject
    protected Events events;
    @Inject
    protected Metadata metadata;
    @Inject
    protected Messages messages;

    public PaletteButton createVerticalLayoutButton() {
        PaletteButton button = createCommonButton();
        button.setCaption(messages.getMainMessage("verticalLayout"));
        button.setIconFromSet(VERTICAL_LAYOUT_ICON);
        button.setLayout(metadata.create(VerticalLayout.class));
        button.setDescription(messages.getMainMessage("verticalLayout"));
        return button;
    }

    public PaletteButton createHorizontalLayoutButton() {
        PaletteButton button = createCommonButton();
        button.setCaption(messages.getMainMessage("horizontalLayout"));
        button.setIconFromSet(HORIZONTAL_LAYOUT_ICON);
        button.setLayout(metadata.create(HorizontalLayout.class));
        button.setDescription(messages.getMainMessage("horizontalLayout"));
        return button;
    }

    public PaletteButton createGridLayoutButton() {
        PaletteButton button = createCommonButton();
        button.setCaption(messages.getMainMessage("gridLayout"));
        button.setIconFromSet(GRID_LAYOUT_ICON);
        button.setLayout(metadata.create(GridLayout.class));
        button.setDescription(messages.getMainMessage("gridLayout"));
        return button;
    }

    public PaletteButton createWidgetButton(Widget widget) {
        WidgetLayout layout = metadata.create(WidgetLayout.class);
        layout.setWidget(widget);

        PaletteButton button = createCommonButton();
        button.setCaption(widget.getCaption());
        button.setDescription(widget.getDescription());
        button.setLayout(layout);
        return button;
    }

    protected PaletteButton createCommonButton(){
        PaletteButton button = new PaletteButton();
        button.setWidth("100%");
        button.setHeight("50px");
        button.setStyleName(AMXD_DASHBOARD_BUTTON);
        return button;
    }
}
