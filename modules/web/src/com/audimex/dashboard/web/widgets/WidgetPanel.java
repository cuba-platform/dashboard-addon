/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.widgets;

import com.audimex.dashboard.entity.DemoContentType;
import com.audimex.dashboard.web.ComponentDescriptor;
import com.audimex.dashboard.web.repo.WidgetRepository;
import com.audimex.dashboard.web.settings.DashboardSettings;
import com.audimex.dashboard.web.utils.DashboardUtils;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.BoxLayout;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.cuba.web.App;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.Map;

public class WidgetPanel extends CssLayout {
    public static final String LAYOUT_CARD_HEADER = "v-panel-caption";

    private VerticalLayout contentLayout = new VerticalLayout();

    private Label headerLabel;

    private int weight = 1;
    private int colSpan = 1;
    private int rowSpan = 1;

    private DemoContentType demoContentType;

    protected DashboardSettings dashboardSettings;

    private ComponentDescriptor componentDescriptor;

    public WidgetPanel() {
        dashboardSettings = AppBeans.get(DashboardSettings.class);
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidth("100%");
        horizontalLayout.setStyleName(LAYOUT_CARD_HEADER);
        setStyleName(ValoTheme.LAYOUT_CARD);

        headerLabel = new Label("Widget");
        headerLabel.setIcon(FontAwesome.ROCKET);
        headerLabel.addStyleName("ad-header-icon");
        horizontalLayout.addComponent(headerLabel);
        horizontalLayout.setExpandRatio(headerLabel, 1);

        Button optionsButton = new Button();
        optionsButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        optionsButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        optionsButton.addStyleName("ad-header-options-icon");
        optionsButton.setIcon(FontAwesome.COG);
        optionsButton.addClickListener((Button.ClickListener) (event) -> {
            WidgetConfigDialog dialog = new WidgetConfigDialog(this);
            getUI().addWindow(dialog);
        });
        horizontalLayout.addComponent(optionsButton);
        horizontalLayout.setComponentAlignment(optionsButton, Alignment.MIDDLE_RIGHT);

        addComponent(horizontalLayout);

        contentLayout.setSizeFull();
        contentLayout.setStyleName("ad-widget-content");
        contentLayout.setMargin(true);

        addComponent(contentLayout);

        setSizeFull();
    }

    public void setContent(Map<String, Object> widgetMap) {
        WidgetRepository.Widget widget = (WidgetRepository.Widget) widgetMap.get("widget");
        Frame parentFrame = (Frame) widgetMap.get("frame");
        WindowManager windowManager = App.getInstance().getWindowManager();
        WindowConfig windowConfig = AppBeans.get(WindowConfig.class);
        WindowInfo windowInfo = windowConfig.getWindowInfo(widget.getFrameId());


        Frame frame = windowManager.openFrame(parentFrame, null, windowInfo);
        frame.setParent(parentFrame);
        setContent(frame.unwrapComposition(Layout.class));
    }

    public void setContent(Component c) {
        contentLayout.removeAllComponents();

        if (c != null) {
            c.setSizeFull();
            contentLayout.addComponent(c);
        }
    }

    public String getHeaderText() {
        if (componentDescriptor != null) {
            return componentDescriptor.getCaption();
        }

        return headerLabel.getValue();
    }

    public void setHeaderText(String text) {
        headerLabel.setValue(text);

        if (componentDescriptor != null) {
            componentDescriptor.setCaption(text);
        }
    }

    public Resource getHeaderIcon() {
        if (componentDescriptor != null) {
            return DashboardUtils.iconNames.inverse().get(componentDescriptor.getIcon());
        }

        return headerLabel.getIcon();
    }

    public void setHeaderIcon(Resource icon) {
        headerLabel.setIcon(icon);

        if (componentDescriptor != null) {
            String iconName = DashboardUtils.iconNames.get(icon);
            componentDescriptor.setIcon(iconName);
        }
    }

    public int getWeight() {
        if (componentDescriptor != null) {
            return componentDescriptor.getWeight();
        }

        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;

        if (componentDescriptor != null) {
            componentDescriptor.setWeight(weight);
        }

        if (getParent() instanceof AbstractOrderedLayout) {
            ((AbstractOrderedLayout) getParent()).setExpandRatio(this, weight);
        }
    }

    public int getColSpan() {
        if (componentDescriptor != null) {
            return componentDescriptor.getColSpan();
        }

        return colSpan;
    }

    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;

        if (componentDescriptor != null) {
            componentDescriptor.setColSpan(colSpan);
        }

        if (getParent() instanceof GridLayout) {
            GridLayout parent = (GridLayout) getParent();

            parent.removeComponent(this);

            parent = DashboardUtils.removeEmptyLabels(parent);
            parent.addComponent(this, componentDescriptor.getColumn(), componentDescriptor.getRow(),
                    componentDescriptor.getColumn() + componentDescriptor.getColSpan() - 1,
                    componentDescriptor.getRow() + componentDescriptor.getRowSpan() - 1);
//            DashboardUtils.addEmptyLabels(parent);
        }
    }

    public int getRowSpan() {
        if (componentDescriptor != null) {
            return componentDescriptor.getRowSpan();
        }

        return rowSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;

        if (componentDescriptor != null) {
            componentDescriptor.setRowSpan(rowSpan);
        }

        if (getParent() instanceof GridLayout) {
            GridLayout parent = (GridLayout) getParent();
            parent.removeComponent(this);

            parent = DashboardUtils.removeEmptyLabels(parent);
            parent.addComponent(this, componentDescriptor.getColumn(), componentDescriptor.getRow(),
                    componentDescriptor.getColumn() + componentDescriptor.getColSpan() - 1,
                    componentDescriptor.getRow() + componentDescriptor.getRowSpan() - 1);
//            DashboardUtils.addEmptyLabels(parent);
        }
    }

    private String getWindowInfo(DemoContentType demoContentType) {
        switch (demoContentType) {
            case INVOICE_REPORT:
                return "demoReportWidget";
            case PRODUCTS_TABLE:
                return "demoTableWidget";
            case SALES_CHART:
                return "demoChartWidget";
        }
        return null;
    }

    public DemoContentType getDemoContentType() {
        if (componentDescriptor != null) {
            return componentDescriptor.getDemoContentType();
        }

        return demoContentType;
    }

    public void setDemoContentType(DemoContentType demoContentType) {
        this.demoContentType = demoContentType;

        if (componentDescriptor != null) {
            componentDescriptor.setDemoContentType(demoContentType);
        }

        if (demoContentType == null) {
//            setContent(null);
        } else {
            WindowManager windowManager = App.getInstance().getWindowManager();
            WindowConfig windowConfig = AppBeans.get(WindowConfig.class);
            WindowInfo windowInfo = windowConfig.getWindowInfo(getWindowInfo(demoContentType));

//            Frame frame = windowManager.openFrame(dashboardContainer.getFrame(), null, windowInfo);
//            frame.setParent(dashboardContainer);
//            setContent(frame.unwrapComposition(Layout.class));
        }
    }

    public ComponentDescriptor getComponentDescriptor() {
        return componentDescriptor;
    }

    public void setComponentDescriptor(ComponentDescriptor componentDescriptor) {
        this.componentDescriptor = componentDescriptor;
    }
}