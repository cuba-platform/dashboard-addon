/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.frames;

import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.model.visual_model.LayoutType;
import com.haulmont.addon.dnd.components.DDVerticalLayout;
import com.haulmont.addon.dnd.components.DropHandler;
import com.haulmont.addon.dnd.components.acceptcriterion.AcceptCriterion;
import com.haulmont.addon.dnd.components.dragevent.DragAndDropEvent;
import com.haulmont.addon.dnd.web.gui.components.AcceptCriterionWrapper;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.vaadin.server.PaintTarget;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.audimex.dashboard.web.DashboardIcon.*;
import static java.util.Collections.emptyList;

public class PaletteFrame extends AbstractFrame {
    public static final String WIDGETS = "WIDGETS";

    @Inject
    protected DDVerticalLayout palette;
    @Inject
    protected Messages messages;

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
        palette.setDropHandler(new DropHandler() {
            @Override
            public void drop(DragAndDropEvent event) {

            }

            @Override
            public AcceptCriterion getCriterion() {
                return (AcceptCriterionWrapper) () -> new com.vaadin.event.dd.acceptcriteria.AcceptCriterion() {
                    @Override
                    public boolean isClientSideVerifiable() {
                        return true;
                    }

                    @Override
                    public void paint(PaintTarget target) {

                    }

                    @Override
                    public void paintResponse(PaintTarget target) {

                    }

                    @Override
                    public boolean accept(com.vaadin.event.dd.DragAndDropEvent dragEvent) {
                        return false;
                    }
                };
            }
        });
    }

    protected void addButtons() {
        PaletteButton verticalLayoutBtn = new PaletteButton(
                getMessage("verticalLayout"),
                VERTICAL_LAYOUT_ICON,
                LayoutType.VERTICAL_LAYOUT,
                null
        );

        PaletteButton horizontalLayoutBtn = new PaletteButton(
                getMessage("horizontalLayout"),
                HORIZONTAL_LAYOUT_ICON,
                LayoutType.HORIZONTAL_LAYOUT,
                null
        );

        PaletteButton gridLayoutBtn = new PaletteButton(
                getMessage("gridLayout"),
                GRID_LAYOUT_ICON,
                LayoutType.GRID_LAYOUT,
                null
        );

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
            PaletteButton btn = new PaletteButton(
                    messages.getMainMessage(widget.getCaption() != null ? widget.getCaption() : ""),
                    widget.getIcon() != null ? widget.getIcon() : "",
                    LayoutType.FRAME_PANEL,
                    widget.getUuid()
            );
            widgetButtons.add(btn);
        }

        return widgetButtons;
    }
}
