/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.frames.palette;

import com.audimex.dashboard.gui.components.PaletteButton;
import com.audimex.dashboard.model.Widget;
import com.haulmont.addon.dnd.components.DDVerticalLayout;
import com.haulmont.addon.dnd.components.DropHandler;
import com.haulmont.addon.dnd.components.acceptcriterion.AcceptCriterion;
import com.haulmont.addon.dnd.components.dragevent.DragAndDropEvent;
import com.haulmont.addon.dnd.web.gui.components.AcceptCriterionWrapper;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.vaadin.server.PaintTarget;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.audimex.dashboard.model.visual_model.LayoutType.*;
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

    //todo refactoring
    protected void addButtons() {
        PaletteButton verticalLayoutBtn = factory.createComponent(PaletteButton.class);
        verticalLayoutBtn.setCaption(getMessage("verticalLayout"));
        verticalLayoutBtn.setIconFromSet(VERTICAL_LAYOUT_ICON);
        verticalLayoutBtn.setLayoutType(VERTICAL_LAYOUT);
        verticalLayoutBtn.setWidth("100%");
        verticalLayoutBtn.setHeight("50px");
        verticalLayoutBtn.setStyleName(AMXD_DASHBOARD_BUTTON);

        PaletteButton horizontalLayoutBtn = factory.createComponent(PaletteButton.class);
        horizontalLayoutBtn.setCaption(getMessage("horizontalLayout"));
        horizontalLayoutBtn.setIconFromSet(HORIZONTAL_LAYOUT_ICON);
        horizontalLayoutBtn.setLayoutType(HORIZONTAL_LAYOUT);
        horizontalLayoutBtn.setWidth("100%");
        horizontalLayoutBtn.setHeight("50px");
        horizontalLayoutBtn.setStyleName(AMXD_DASHBOARD_BUTTON);

        PaletteButton gridLayoutBtn = factory.createComponent(PaletteButton.class);
        gridLayoutBtn.setCaption(getMessage("gridLayout"));
        gridLayoutBtn.setIconFromSet(GRID_LAYOUT_ICON);
        gridLayoutBtn.setLayoutType(GRID_LAYOUT);
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
            btn.setLayoutType(FRAME_PANEL);
            btn.setWidgetUuid(widget.getUuid());
            btn.setWidth("100%");
            btn.setHeight("50px");
            btn.setStyleName(AMXD_DASHBOARD_BUTTON);

            widgetButtons.add(btn);
        }

        return widgetButtons;
    }
}
