/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.frames;

import com.audimex.dashboard.model.Widget;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.vaadin.ui.Layout;
import fi.jasoft.dragdroplayouts.DDVerticalLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;
import fi.jasoft.dragdroplayouts.drophandlers.DefaultVerticalLayoutDropHandler;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WidgetsPaletteFrame extends AbstractFrame {
    public static final String WIDGETS_PARAM = "WIDGETS_PARAM";

    @Inject
    protected VBoxLayout widgetsContainer;
//    @Inject
//    protected IconResolver iconResolver;

    protected DDVerticalLayout ddVerticalLayout;

    protected List<Widget> widgets;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        setParams(params);
        configDDLayout();

        ((Layout) WebComponentsHelper.unwrap(widgetsContainer)).addComponent(ddVerticalLayout);
    }

    protected void setParams(Map<String, Object> params) {
        widgets = (List<Widget>) params.getOrDefault(WIDGETS_PARAM, new ArrayList<Widget>());
    }

    protected void configDDLayout() {
        ddVerticalLayout = new DDVerticalLayout();
        ddVerticalLayout.setDragMode(LayoutDragMode.CLONE);
        ddVerticalLayout.setDropHandler(new DefaultVerticalLayoutDropHandler());

        for (Widget widget : widgets) {
            String caption = widget.getCaption() != null ? widget.getCaption() : "";
            String icon = widget.getIcon() != null ? widget.getIcon() : "";

            PaletteButton paletteButton = new PaletteButton(
                    messages.getMainMessage(caption), null
//                    iconResolver.getIconResource(icon)
            );
            paletteButton.setWidget(widget);
            ddVerticalLayout.addComponent(paletteButton);
        }
    }


}
