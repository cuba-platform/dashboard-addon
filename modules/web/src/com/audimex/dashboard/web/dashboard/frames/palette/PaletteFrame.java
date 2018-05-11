/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.frames.palette;

import com.audimex.dashboard.gui.components.PaletteButton;
import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.model.visual_model.WidgetLayout;
import com.audimex.dashboard.model.visual_model.GridLayout;
import com.audimex.dashboard.model.visual_model.HorizontalLayout;
import com.audimex.dashboard.model.visual_model.VerticalLayout;
import com.audimex.dashboard.web.dashboard.drop_handlers.NotDropHandler;
import com.haulmont.addon.dnd.components.DDVerticalLayout;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.audimex.dashboard.web.DashboardIcon.*;
import static com.audimex.dashboard.web.DashboardStyleConstants.AMXD_DASHBOARD_BUTTON;
import static java.util.Collections.emptyList;

public class PaletteFrame extends AbstractFrame {
    public static final String WIDGETS = "WIDGETS";

    @Inject
    protected DDVerticalLayout palette;
    @Inject
    protected ComponentsFactory factory;
    @Inject
    protected Messages messages;
    @Inject
    protected Metadata metadata;
    @Inject
    protected NotDropHandler handler;

    protected List<Widget> widgets;


    //todo: icons not show
    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        widgets = (List<Widget>) params.getOrDefault(WIDGETS, emptyList());
        setPaletteSettings();
        addButtons();
    }

    protected void setPaletteSettings() {
        palette.setDropHandler(handler);
    }

    //todo refactoring
    protected void addButtons() {
        PaletteButton verticalLayoutBtn = factory.createComponent(PaletteButton.class);
        verticalLayoutBtn.setCaption(getMessage("verticalLayout"));
        verticalLayoutBtn.setIconFromSet(VERTICAL_LAYOUT_ICON);
        verticalLayoutBtn.setLayout(metadata.create(VerticalLayout.class));
        verticalLayoutBtn.setWidth("100%");
        verticalLayoutBtn.setHeight("50px");
        verticalLayoutBtn.setStyleName(AMXD_DASHBOARD_BUTTON);

        PaletteButton horizontalLayoutBtn = factory.createComponent(PaletteButton.class);
        horizontalLayoutBtn.setCaption(getMessage("horizontalLayout"));
        horizontalLayoutBtn.setIconFromSet(HORIZONTAL_LAYOUT_ICON);
        horizontalLayoutBtn.setLayout(metadata.create(HorizontalLayout.class));
        horizontalLayoutBtn.setWidth("100%");
        horizontalLayoutBtn.setHeight("50px");
        horizontalLayoutBtn.setStyleName(AMXD_DASHBOARD_BUTTON);

        PaletteButton gridLayoutBtn = factory.createComponent(PaletteButton.class);
        gridLayoutBtn.setCaption(getMessage("gridLayout"));
        gridLayoutBtn.setIconFromSet(GRID_LAYOUT_ICON);
        gridLayoutBtn.setLayout(metadata.create(GridLayout.class));
        gridLayoutBtn.setWidth("100%");
        gridLayoutBtn.setHeight("50px");
        gridLayoutBtn.setStyleName(AMXD_DASHBOARD_BUTTON);

        palette.add(verticalLayoutBtn);
        palette.add(horizontalLayoutBtn);
        palette.add(gridLayoutBtn);

        for (PaletteButton widgetBtn : createWidgetButtons()) {
            palette.add(widgetBtn);
        }
    }

    protected List<PaletteButton> createWidgetButtons() {
        List<PaletteButton> widgetButtons = new ArrayList<>();

        for (Widget widget : widgets) {
            PaletteButton btn = factory.createComponent(PaletteButton.class);
            btn.setCaption(messages.getMainMessage(widget.getCaption() != null ? widget.getCaption() : ""));
            btn.setIcon(widget.getIcon() != null ? widget.getIcon() : "");
            WidgetLayout widgetLayout = metadata.create(WidgetLayout.class);
            widgetLayout.setWidget(widget);
            btn.setLayout(widgetLayout);
            btn.setWidth("100%");
            btn.setHeight("50px");
            btn.setStyleName(AMXD_DASHBOARD_BUTTON);

            widgetButtons.add(btn);
        }

        return widgetButtons;
    }
}
