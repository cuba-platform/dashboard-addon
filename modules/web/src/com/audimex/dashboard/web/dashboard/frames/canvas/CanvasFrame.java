package com.audimex.dashboard.web.dashboard.frames.canvas;

import com.audimex.dashboard.web.dashboard.drop_handlers.VerticalLayoutDropHandler;
import com.haulmont.addon.dnd.components.DDVerticalLayout;
import com.haulmont.cuba.gui.components.AbstractFrame;

import javax.inject.Inject;
import java.util.Map;

public class CanvasFrame extends AbstractFrame {
    @Inject
    protected DDVerticalLayout canvas;
    @Inject
    protected VerticalLayoutDropHandler handler;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        setDashboardSettings();
    }

    protected void setDashboardSettings() {
        canvas.setDropHandler(handler);
    }
}
