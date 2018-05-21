package com.audimex.dashboard.web.dashboard.frames.canvas;

import com.audimex.dashboard.model.visual_model.*;
import com.audimex.dashboard.web.dashboard.tools.DashboardModelConverter;
import com.audimex.dashboard.web.dashboard.tools.DropLayoutTool;
import com.haulmont.addon.dnd.components.DDVerticalLayout;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Component;

import javax.inject.Inject;
import java.util.Map;

public class CanvasFrame extends AbstractFrame {
    public static final String VISUAL_MODEL = "VISUAL_MODEL";

    @Inject
    protected DDVerticalLayout canvas;
    @Inject
    protected DropLayoutTool tool;
    @Inject
    protected DashboardModelConverter converter;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        initDashboardModel(params);
        tool.init(this, canvas);
    }

    public VerticalLayout getDashboardModel() {
        return converter.containerToModel(canvas);
    }

    protected void initDashboardModel(Map<String, Object> params) {
        DashboardLayout model = (DashboardLayout) params.get(VISUAL_MODEL);

        if (model != null) {
            Container container = converter.modelToContainer(this, model);
            for (Component component : container.getOwnComponents()) {
                component.setParent(null);
                canvas.add(component);
            }
        }
    }

}
