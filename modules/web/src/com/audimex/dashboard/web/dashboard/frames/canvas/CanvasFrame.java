/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.frames.canvas;

import com.audimex.dashboard.model.visual_model.VerticalLayout;
import com.audimex.dashboard.web.dashboard.tools.DashboardModelConverter;
import com.audimex.dashboard.web.dashboard.tools.VaadinComponentsFactory;
import com.audimex.dashboard.web.dashboard.vaadin_components.layouts.CanvasVerticalLayout;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.vaadin.ui.Layout;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class CanvasFrame extends AbstractFrame {
    public static final String SCREEN_NAME = "canvasFrame";
    public static final String VISUAL_MODEL = "VISUAL_MODEL";

    @Inject
    protected VBoxLayout canvas;
    @Inject
    protected DashboardModelConverter converter;
    @Named("amdx_VaadinUiComponentsFactory")
    protected VaadinComponentsFactory factory;

    protected CanvasVerticalLayout vLayout;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initLayout(factory, params);
    }

    public VerticalLayout getDashboardModel() {
        return converter.containerToModel(vLayout);
    }

    protected void initLayout(VaadinComponentsFactory factory, Map<String, Object> params) {
        converter.setFactory(factory);
        vLayout = factory.createCanvasVerticalLayout();

        VerticalLayout model = (VerticalLayout) params.get(VISUAL_MODEL);

        if (model != null) {
            vLayout = (CanvasVerticalLayout) converter.modelToContainer(this, model);
        }

        canvas.unwrap(Layout.class).addComponent(vLayout);
    }
}
