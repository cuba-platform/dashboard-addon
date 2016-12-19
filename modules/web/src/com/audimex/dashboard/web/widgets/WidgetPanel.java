/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.widgets;

import com.audimex.dashboard.entity.DemoContentType;
import com.audimex.dashboard.web.ComponentDescriptor;
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

public class WidgetPanel extends CssLayout {
    public static final String LAYOUT_CARD_HEADER = "v-panel-caption";

    private VerticalLayout contentLayout = new VerticalLayout();

    private Label headerLabel;

    private Integer weight;
    private Integer colSpan;
    private Integer rowSpan;
    private DemoContentType demoContentType;

    private ComponentDescriptor componentDescriptor;
    private BoxLayout dashboardContainer;

    public WidgetPanel(BoxLayout dashboardContainer) {
        this.dashboardContainer = dashboardContainer;

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

    public void setContent(Component c) {
        if (c == null) {
            contentLayout.removeAllComponents();
        } else {
            c.setSizeFull();

            contentLayout.addComponent(c);
        }
    }

    public String getHeaderText() {
        return headerLabel.getValue();
    }

    public void setHeaderText(String text) {
        headerLabel.setValue(text);
    }

    public Resource getHeaderIcon() {
        return headerLabel.getIcon();
    }

    public void setHeaderIcon(Resource icon) {
        headerLabel.setIcon(icon);
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setColSpan(Integer colSpan) {
        this.colSpan = colSpan;
    }

    public Integer getColSpan() {
        return colSpan;
    }

    public void setRowSpan(Integer rowSpan) {
        this.rowSpan = rowSpan;
    }

    public Integer getRowSpan() {
        return rowSpan;
    }

    public void setDemoContentType(DemoContentType demoContentType) {
        this.demoContentType = demoContentType;

        if (demoContentType == null) {
            setContent(null);
        } else {
            WindowManager windowManager = App.getInstance().getWindowManager();
            WindowConfig windowConfig = AppBeans.get(WindowConfig.class);
            WindowInfo windowInfo = windowConfig.getWindowInfo(getWindowInfo(demoContentType));

            Frame frame = windowManager.openFrame(dashboardContainer.getFrame(), null, windowInfo);
            frame.setParent(dashboardContainer);
            setContent(frame.unwrapComposition(Layout.class));
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
        return demoContentType;
    }

    public ComponentDescriptor getComponentDescriptor() {
        return componentDescriptor;
    }

    public void setComponentDescriptor(ComponentDescriptor componentDescriptor) {
        this.componentDescriptor = componentDescriptor;
    }
}