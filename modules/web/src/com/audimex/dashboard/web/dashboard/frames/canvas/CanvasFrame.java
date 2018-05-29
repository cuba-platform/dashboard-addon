/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.frames.canvas;

import com.audimex.dashboard.model.visual_model.VerticalLayout;
import com.audimex.dashboard.web.dashboard.tools.DashboardModelConverter;
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
    @Named("uiModelConverter")
    protected DashboardModelConverter converter;

    protected CanvasVerticalLayout vLayout;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initLayout(params);
    }

    public VerticalLayout getDashboardModel() {
        return getConverter().containerToModel(vLayout);
    }

    protected DashboardModelConverter getConverter() {
        return converter;
    }

    protected void initLayout(Map<String, Object> params) {
        VerticalLayout model = (VerticalLayout) params.get(VISUAL_MODEL);

        if (model != null) {
            vLayout = (CanvasVerticalLayout) getConverter().modelToContainer(this, model);
        } else {
            vLayout = getConverter().getFactory().createCanvasVerticalLayout();
        }

        canvas.unwrap(Layout.class).addComponent(vLayout);
    }
}
