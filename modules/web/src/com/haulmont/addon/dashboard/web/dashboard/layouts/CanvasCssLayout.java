/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.layouts;

import com.haulmont.addon.dashboard.model.visualmodel.CssLayout;
import com.haulmont.addon.dashboard.web.dashboard.frames.uicomponent.WebDashboardFrame;
import com.haulmont.addon.dnd.web.gui.components.WebDDCssLayout;
import com.haulmont.bali.events.Subscription;
import com.haulmont.cuba.gui.components.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class CanvasCssLayout extends AbstractCanvasLayout {

    private static Logger log = LoggerFactory.getLogger(WebDashboardFrame.class);

    protected WebDDCssLayout cssLayout;

    public CanvasCssLayout(CssLayout cssLayoutModel) {
        super(cssLayoutModel, new WebDDCssLayout());
        cssLayout = (WebDDCssLayout) delegate;

        cssLayout.setStyleName(cssLayoutModel.getStyleName());
        cssLayout.setResponsive(cssLayoutModel.getResponsive());

    }

    @Override
    public WebDDCssLayout getDelegate() {
        return cssLayout;
    }

    public void addComponent(Component component) {
        cssLayout.add(component);
    }

    @Override
    public Subscription addLayoutClickListener(Consumer<LayoutClickEvent> listener) {
        log.info("Click listener is not supported yet in Css layout");
        return () -> {};
    }

    @Override
    public void removeLayoutClickListener(Consumer<LayoutClickEvent> listener) {
        log.info("Click listener is not supported yet in Css layout");
    }

}
