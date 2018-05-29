/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.frames.editor.palette;

import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.model.visual_model.GridLayout;
import com.audimex.dashboard.model.visual_model.HorizontalLayout;
import com.audimex.dashboard.model.visual_model.VerticalLayout;
import com.audimex.dashboard.model.visual_model.WidgetLayout;
import com.audimex.dashboard.web.dashboard.drop_handlers.NotDropHandler;
import com.audimex.dashboard.web.dashboard.vaadin_components.PaletteButton;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.web.gui.icons.IconResolver;
import com.vaadin.ui.Layout;
import fi.jasoft.dragdroplayouts.DDVerticalLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static com.audimex.dashboard.web.DashboardIcon.*;
import static java.util.Collections.emptyList;

public class PaletteFrame extends AbstractFrame {
    public static final String SCREEN_NAME = "paletteFrame";
    public static final String WIDGETS = "WIDGETS";

    @Inject
    protected VBoxLayout palette;
    @Inject
    protected Messages messages;
    @Inject
    protected Metadata metadata;
    @Inject
    protected IconResolver iconResolver;

    protected List<Widget> widgets;
    protected DDVerticalLayout ddPalette = new DDVerticalLayout();

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        widgets = (List<Widget>) params.getOrDefault(WIDGETS, emptyList());

        ddPalette.setDragMode(LayoutDragMode.CLONE);
        ddPalette.setDropHandler(new NotDropHandler());
        palette.unwrap(Layout.class).addComponent(ddPalette);
        addLayoutButtons();
        addWidgetButtons();
    }

    protected void addLayoutButtons() {
        PaletteButton verticalLayoutBtn = new PaletteButton(
                getMessage("verticalLayout"),
                iconResolver.getIconResource(VERTICAL_LAYOUT_ICON.source()),
                metadata.create(VerticalLayout.class)
        );

        PaletteButton horizontalLayoutBtn = new PaletteButton(
                getMessage("horizontalLayout"),
                iconResolver.getIconResource(HORIZONTAL_LAYOUT_ICON.source()),
                metadata.create(HorizontalLayout.class)
        );

        PaletteButton gridLayoutBtn = new PaletteButton(
                getMessage("gridLayout"),
                iconResolver.getIconResource(GRID_LAYOUT_ICON.source()),
                metadata.create(GridLayout.class)
        );

        ddPalette.addComponent(verticalLayoutBtn);
        ddPalette.addComponent(horizontalLayoutBtn);
        ddPalette.addComponent(gridLayoutBtn);
    }

    protected void addWidgetButtons() {
        for (Widget widget : widgets) {
            WidgetLayout widgetLayout = metadata.create(WidgetLayout.class);
            widgetLayout.setWidget(widget);

            PaletteButton widgetBtn = new PaletteButton(
                    messages.getMainMessage(widget.getCaption() != null ? widget.getCaption() : ""),
                    iconResolver.getIconResource(widget.getIcon() != null ? widget.getIcon() : ""),
                    widgetLayout
            );

            ddPalette.addComponent(widgetBtn);
        }
    }
}
