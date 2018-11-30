/*
 * Copyright (c) 2008-2018 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.haulmont.addon.dashboard.web.dashboard.frames.editor.weight;

import com.haulmont.addon.dashboard.model.visualmodel.DashboardLayout;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.HBoxLayout;
import com.vaadin.data.HasValue;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Slider;

import javax.inject.Inject;
import java.util.Map;

public class WeightDialog extends AbstractWindow {
    public static final String SCREEN_NAME = "dashboard$WeightDialog";
    public static final String WIDGET = "WIDGET";

    @Inject
    protected HBoxLayout sliderBox;

    protected Slider weightSlider = new Slider(1, 16);
    private DashboardLayout layout;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        layout = (DashboardLayout) params.get(WIDGET);

        int initWeight = layout.getWeight();

        weightSlider.setValue((double) initWeight);
        weightSlider.setCaption(formatMessage("dashboard.weight", initWeight));
        weightSlider.setWidth(100, Sizeable.Unit.PERCENTAGE);
        weightSlider.setCaptionAsHtml(true);

        weightSlider.addValueChangeListener(event ->
                weightSlider.setCaption(formatMessage("dashboard.weight", weightSlider.getValue().intValue()))
        );
        weightSlider.focus();

        sliderBox.unwrap(Layout.class).addComponent(weightSlider);
    }

    public int getWeight() {
        return weightSlider.getValue().intValue();
    }

    public void apply() {
        layout.setWeight(weightSlider.getValue().intValue());
        //TODO remove deprecaded code
        this.close(COMMIT_ACTION_ID);
    }

    //TODO remove deprecaded code
    public void cancel() {
        this.close(CLOSE_ACTION_ID);
    }
}