/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web;

import com.vaadin.ui.*;

public class WidgetPanel extends CssLayout {
    protected String caption;
    protected VerticalLayout contentLayout = new VerticalLayout();

    public WidgetPanel(String caption) {
        this.caption = caption;
        buildComponent();
    }

    protected void buildComponent() {
        removeAllComponents();
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidth("100%");
        horizontalLayout.setStyleName("v-panel-caption");
        setStyleName("card");

        Label label = new Label(caption);
        horizontalLayout.addComponent(label);
        addComponent(horizontalLayout);

        contentLayout.setHeight("100%");
        addComponent(contentLayout);
    }

    public void addComponentToContainer(Component c) {
        contentLayout.addComponent(c);
    }
}
