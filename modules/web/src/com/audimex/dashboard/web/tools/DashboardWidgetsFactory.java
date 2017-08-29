package com.audimex.dashboard.web.tools;

import com.audimex.dashboard.entity.*;
import com.audimex.dashboard.web.dashboard.PaletteButton;
import com.audimex.dashboard.web.dashboard.events.DashboardEvent;
import com.audimex.dashboard.web.dashboard.events.DashboardEventType;
import com.audimex.dashboard.web.drophandlers.GridDropListener;
import com.audimex.dashboard.web.layouts.DashboardGridLayout;
import com.audimex.dashboard.web.layouts.HasDragCaption;
import com.audimex.dashboard.web.widgets.FramePanel;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.Frame;
import com.vaadin.ui.Tree;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.function.Consumer;

@Component(DashboardWidgetsFactory.NAME)
public class DashboardWidgetsFactory {
    public static final String NAME = "amxd_DashboardWidgetsFactory";

    @Inject
    protected ParameterTools parameterTools;

    public com.vaadin.ui.Component createWidgetOnDrop(PaletteButton dragComponent, Tree tree, GridDropListener gridDropListener,
                                                      Frame frame, Consumer<Tree> treeHandler, Object layout) {
        DashboardTools dashboardTools = AppBeans.get(DashboardTools.NAME);
        com.vaadin.ui.Component component = null;

        if (dragComponent.getWidgetType() == WidgetType.VERTICAL_LAYOUT) {
            component = dashboardTools.createVerticalDropLayout(tree, gridDropListener, frame, treeHandler);
        } else if (dragComponent.getWidgetType() == WidgetType.HORIZONTAL_LAYOUT) {
            component = dashboardTools.createHorizontalDropLayout(tree, gridDropListener, frame, treeHandler);
        } else if (dragComponent.getWidgetType() == WidgetType.GRID_LAYOUT) {
            DashboardGridLayout gridLayout = (DashboardGridLayout) dashboardTools.createGridDropLayout(tree,
                    gridDropListener, frame, treeHandler);
            gridDropListener.gridDropped(gridLayout, layout, 0);
            component = gridLayout;
        } else if (dragComponent.getWidgetType() == WidgetType.FRAME_PANEL) {
            DashboardWidget templateWidget = dragComponent.getWidget();
            Consumer<DashboardEvent> dashboardEventConsumer = dragComponent.getDashboardEventExecutor();
            DashboardWidgetLink link = null;

            if (dragComponent.getDashboard() != null && dashboardEventConsumer != null) {
                Dashboard dashboard = dragComponent.getDashboard();
                link = parameterTools.createDashboardLink(dashboard, templateWidget, dashboardEventConsumer);

            }

            switch (templateWidget.getWidgetViewType()) {
                case COMMON:
                    DashboardWidget widget = new DashboardWidget();
                    widget.setCaption(templateWidget.getIcon());
                    widget.setFrameId(templateWidget.getFrameId());
                    if (link != null) {
                        widget.addDashboardLink(link);
                    }

                    component = new FramePanel(tree, dragComponent.getDashboard(), dashboardEventConsumer, widget,
                            frame, treeHandler);

                    ((FramePanel) component).setWidgetCaption(templateWidget.getCaption());
                    ((FramePanel) component).setWidgetIcon(templateWidget.getIcon());
                    ((FramePanel) component).setTemplateWidget(templateWidget);
                    break;
                case LIST:
                    break;
                case CHART:
                    break;
            }
        }

        if (component instanceof HasDragCaption) {
            ((HasDragCaption) component).setWidgetCaption(dragComponent.getWidget().getCaption());
            ((HasDragCaption) component).setWidgetIcon(dragComponent.getWidget().getIcon());
        }

        return component;
    }
}
