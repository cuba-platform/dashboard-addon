package com.audimex.dashboard.web.dashboard.frames.canvas;

import com.audimex.dashboard.model.visual_model.DashboardLayout;
import com.audimex.dashboard.model.visual_model.VerticalLayout;
import com.audimex.dashboard.web.dashboard.converter.DashboardVisualModelConverter;
import com.audimex.dashboard.web.dashboard.drop_handlers.VerticalLayoutDropHandler;
import com.haulmont.addon.dnd.components.DDVerticalLayout;
import com.haulmont.cuba.gui.components.AbstractFrame;

import javax.inject.Inject;
import java.util.Map;

public class CanvasFrame extends AbstractFrame {
    public static final String VISUAL_MODEL = "VISUAL_MODEL";

    @Inject
    protected DDVerticalLayout canvas;
    @Inject
    protected VerticalLayoutDropHandler handler;
    @Inject
    protected DashboardVisualModelConverter converter;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        canvas.setDropHandler(handler);

        DashboardLayout model = (DashboardLayout) params.get(VISUAL_MODEL);

        if (model != null) {
            converter.convertModelToVisual(canvas, model);
        }
    }

    public VerticalLayout getDashboardModel() {
        return converter.convertVisualToModel(canvas);
    }
}
