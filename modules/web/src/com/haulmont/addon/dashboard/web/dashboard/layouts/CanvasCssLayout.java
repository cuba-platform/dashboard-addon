/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.layouts;

import com.haulmont.addon.dashboard.model.visualmodel.CssLayout;
import com.haulmont.addon.dashboard.web.dashboard.frames.uicomponent.WebDashboardFrame;
import com.haulmont.addon.dnd.web.gui.components.WebDDCssLayout;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.ScrollBoxLayout;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CanvasCssLayout extends AbstractCanvasLayout {

    private static Logger log = LoggerFactory.getLogger(WebDashboardFrame.class);

    protected WebDDCssLayout cssLayout;

    public CanvasCssLayout(CssLayout cssLayoutModel) {
        super(cssLayoutModel, AppBeans.get(ComponentsFactory.class).createComponent(ScrollBoxLayout.class));
        cssLayout = new WebDDCssLayout();
        ScrollBoxLayout scrollBox = (ScrollBoxLayout) delegate;
        scrollBox.setSizeFull();
        scrollBox.add(cssLayout);

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
    public void addLayoutClickListener(LayoutClickListener clickListener) {
        log.info("Click listener is not supported yet in Css layout");
    }

    @Override
    public void removeLayoutClickListener(LayoutClickListener listener) {
        log.info("Click listener is not supported yet in Css layout");
    }


}
