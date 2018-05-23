package com.audimex.dashboard.web.dashboard.frames.canvas;

import com.audimex.dashboard.model.visual_model.DashboardLayout;
import com.audimex.dashboard.model.visual_model.VerticalLayout;
import com.audimex.dashboard.web.dashboard.layouts.DdDashboardVerticalLayout;
import com.audimex.dashboard.web.dashboard.tools.DashboardModelConverter;
import com.audimex.dashboard.web.dashboard.tools.DropLayoutTools;
import com.haulmont.cuba.gui.components.*;
import com.vaadin.ui.Layout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;

import javax.inject.Inject;
import java.util.Map;

import static com.audimex.dashboard.web.DashboardStyleConstants.AMXD_SHADOW_BORDER;

public class CanvasFrame extends AbstractFrame {
    public static final String VISUAL_MODEL = "VISUAL_MODEL";

    @Inject
    protected VBoxLayout canvas;
    @Inject
    protected DropLayoutTools tool;
    @Inject
    protected DashboardModelConverter converter;

    protected DdDashboardVerticalLayout ddCanvas = new DdDashboardVerticalLayout();

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        ddCanvas.setDragMode(LayoutDragMode.CLONE);
        ddCanvas.setSizeFull();

        canvas.unwrap(Layout.class).addComponent(ddCanvas);
        ddCanvas.addStyleName(AMXD_SHADOW_BORDER);


//        initDashboardModel(params);
        tool.init(this, ddCanvas);
        addLayoutClickListener();
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
//                ddCanvas.addComponent(component);
            }
        }
    }

    public void addLayoutClickListener() {
        ddCanvas.addLayoutClickListener(event -> {
                    showNotification("asdas");
                    com.vaadin.ui.Component clickedComponent = event.getClickedComponent();


                }
        );
    }

}
