/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.haulmont.addon.dashboard.web.dashboard.frames.editor.weight;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.HBoxLayout;
import com.vaadin.data.Property;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Slider;

import javax.inject.Inject;
import java.util.Map;

public class WeightDialog extends AbstractWindow {
    public static final String SCREEN_NAME = "dashboard$WeightDialog";
    public static final String WEIGHT = "WEIGHT";

    @Inject
    protected HBoxLayout sliderBox;

    protected Slider slider = new Slider(1, 16);

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        int initWeight = (int) params.getOrDefault(WEIGHT, 1);

        slider.setValue((double) initWeight);
        slider.setCaption(formatMessage("dashboard.weight", initWeight));
        slider.setWidth(100, Sizeable.Unit.PERCENTAGE);
        slider.setCaptionAsHtml(true);

        slider.addValueChangeListener((Property.ValueChangeListener) event ->
                slider.setCaption(formatMessage("dashboard.weight", slider.getValue().intValue()))
        );
        slider.focus();

        sliderBox.unwrap(Layout.class).addComponent(slider);
    }

    public int getWeight() {
        return slider.getValue().intValue();
    }

    public void apply() {
        this.close(COMMIT_ACTION_ID);
    }

    public void cancel() {
        this.close(CLOSE_ACTION_ID);
    }
}