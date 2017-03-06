package com.audimex.dashboard.web.tools;

import com.audimex.dashboard.entity.WidgetType;
import com.audimex.dashboard.web.dashboard.PaletteButton;
import com.audimex.dashboard.web.drophandlers.GridDropListener;
import com.audimex.dashboard.web.widgets.FramePanel;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.components.Frame;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Tree;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component(DashboardWidgetsFactory.NAME)
public class DashboardWidgetsFactory {
    public static final String NAME = "amxd_DashboardWidgetsFactory";

    public com.vaadin.ui.Component createWidgetOnDrop(PaletteButton dragComponent, Tree tree,
                                                      GridDropListener gridDropListener, Frame frame,
                                                      Consumer<Tree> treeHandler, Object layout) {
        DashboardTools dashboardTools = AppBeans.get(DashboardTools.NAME);

        if (dragComponent.getWidgetType() == WidgetType.VERTICAL_LAYOUT) {
            return dashboardTools.createVerticalDropLayout(tree, gridDropListener, frame, treeHandler);
        } else if (dragComponent.getWidgetType() == WidgetType.HORIZONTAL_LAYOUT) {
            return dashboardTools.createHorizontalDropLayout(tree, gridDropListener, frame, treeHandler);
        } else if (dragComponent.getWidgetType() == WidgetType.GRID_LAYOUT) {
            GridLayout gridLayout = (GridLayout) dashboardTools.createGridDropLayout(tree, gridDropListener, frame, treeHandler);
            gridDropListener.gridDropped(gridLayout, layout, 0);
            return gridLayout;
        } else if (dragComponent.getWidgetType() == WidgetType.FRAME_PANEL) {
            return new FramePanel(tree, dragComponent.getWidget().getFrameId(), frame, treeHandler);
        } else {
            return null;
        }
    }
}
