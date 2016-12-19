/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.widgets;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

public class WidgetPanel extends CssLayout {
    public static final String LAYOUT_CARD_HEADER = "v-panel-caption";

    protected VerticalLayout contentLayout = new VerticalLayout();

    protected OptionsClickListener optionsClickListener;
    protected Label headerLabel;

    public WidgetPanel() {
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
        addComponent(contentLayout);
    }

    public void setContent(Component c) {
        c.setSizeFull();

        contentLayout.addComponent(c);
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

    public OptionsClickListener getOptionsClickListener() {
        return optionsClickListener;
    }

    public void setOptionsClickListener(OptionsClickListener optionsClickListener) {
        this.optionsClickListener = optionsClickListener;
    }

    public interface OptionsClickListener {
        void optionsClickPerformed(WidgetPanel widget);
    }
}