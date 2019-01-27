/*
 * Copyright (c) 2008-2019 Haulmont.
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
 */
package com.haulmont.addon.dashboard.web.dashboard.frames.editor.responsive;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.HBoxLayout;
import com.vaadin.data.Property;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Slider;

import javax.inject.Inject;
import java.util.Map;

public class ResponsiveCreationDialog extends AbstractWindow {
    public static final String SCREEN_NAME = "dashboard$ResponsiveDialog";

    @Inject
    protected HBoxLayout slidersBox;

    protected Slider xs = new Slider(1, 12);
    protected Slider sm = new Slider(1, 12);
    protected Slider md = new Slider(1, 12);
    protected Slider lg = new Slider(1, 12);

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        xs.setCaption(formatMessage("dashboard.responsive.xs", 12));
        sm.setCaption(formatMessage("dashboard.responsive.sm", 6));
        md.setCaption(formatMessage("dashboard.responsive.md", 6));
        lg.setCaption(formatMessage("dashboard.responsive.lg", 3));

        xs.setValue((double) 12);
        sm.setValue((double) 6);
        md.setValue((double) 6);
        lg.setValue((double) 3);

        xs.setWidth(100, Sizeable.Unit.PERCENTAGE);
        sm.setWidth(100, Sizeable.Unit.PERCENTAGE);
        md.setWidth(100, Sizeable.Unit.PERCENTAGE);
        lg.setWidth(100, Sizeable.Unit.PERCENTAGE);

        xs.setCaptionAsHtml(true);
        sm.setCaptionAsHtml(true);
        md.setCaptionAsHtml(true);
        lg.setCaptionAsHtml(true);

        xs.addValueChangeListener((Property.ValueChangeListener) event ->
                xs.setCaption(formatMessage("dashboard.responsive.xs", xs.getValue().intValue()))
        );
        sm.addValueChangeListener((Property.ValueChangeListener) event ->
                sm.setCaption(formatMessage("dashboard.responsive.sm", sm.getValue().intValue()))
        );
        md.addValueChangeListener((Property.ValueChangeListener) event ->
                md.setCaption(formatMessage("dashboard.responsive.md", md.getValue().intValue()))
        );
        lg.addValueChangeListener((Property.ValueChangeListener) event ->
                lg.setCaption(formatMessage("dashboard.responsive.lg", lg.getValue().intValue()))
        );

        xs.focus();

        Layout hbox = slidersBox.unwrap(Layout.class);
        hbox.addComponent(xs);
        hbox.addComponent(sm);
        hbox.addComponent(md);
        hbox.addComponent(lg);
    }

    public void apply() {
        this.close(COMMIT_ACTION_ID);
    }

    public void cancel() {
        this.close(CLOSE_ACTION_ID);
    }

    public int getXs() {
        return xs.getValue().intValue();
    }

    public int getSm() {
        return sm.getValue().intValue();
    }

    public int getMd() {
        return md.getValue().intValue();
    }

    public int getLg() {
        return lg.getValue().intValue();
    }
}