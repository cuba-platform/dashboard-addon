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
import com.haulmont.cuba.gui.components.BoxLayout;
import com.haulmont.cuba.gui.screen.StandardCloseAction;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Slider;

import javax.inject.Inject;
import java.util.Map;

import static org.strangeway.responsive.web.components.ResponsiveLayout.DisplaySize.*;

@UiController("dashboard$ResponsiveDialog")
@UiDescriptor("responsive-creation-dialog.xml")
public class ResponsiveCreationDialog extends AbstractWindow {
    public static final String SCREEN_NAME = "dashboard$ResponsiveDialog";

    @Inject
    protected BoxLayout slidersBox;
    @Inject
    private BoxLayout desktopBox;
    @Inject
    private BoxLayout mobileBox;

    protected Slider xs = new Slider(1, 12);
    protected Slider sm = new Slider(1, 12);
    protected Slider md = new Slider(1, 12);
    protected Slider lg = new Slider(1, 12);

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        double xsd = params.get(XS.name()) == null ? (double) 12 : (double) params.get(XS.name());
        double smd = params.get(SM.name()) == null ? (double) 6 : (double) params.get(SM.name());
        double mdd = params.get(MD.name()) == null ? (double) 6 : (double) params.get(MD.name());
        double lgd = params.get(LG.name()) == null ? (double) 3 : (double) params.get(LG.name());

        xs.setCaption(formatMessage("dashboard.responsive.xs", (int) xsd));
        sm.setCaption(formatMessage("dashboard.responsive.sm", (int) smd));
        md.setCaption(formatMessage("dashboard.responsive.md", (int) mdd));
        lg.setCaption(formatMessage("dashboard.responsive.lg", (int) lgd));

        xs.setValue(xsd);
        sm.setValue(smd);
        md.setValue(mdd);
        lg.setValue(lgd);

        xs.setWidth(100, Sizeable.Unit.PERCENTAGE);
        sm.setWidth(100, Sizeable.Unit.PERCENTAGE);
        md.setWidth(100, Sizeable.Unit.PERCENTAGE);
        lg.setWidth(100, Sizeable.Unit.PERCENTAGE);

        xs.setCaptionAsHtml(true);
        sm.setCaptionAsHtml(true);
        md.setCaptionAsHtml(true);
        lg.setCaptionAsHtml(true);

        xs.addValueChangeListener(event ->
                xs.setCaption(formatMessage("dashboard.responsive.xs", xs.getValue().intValue()))
        );
        sm.addValueChangeListener(event ->
                sm.setCaption(formatMessage("dashboard.responsive.sm", sm.getValue().intValue()))
        );
        md.addValueChangeListener(event ->
                md.setCaption(formatMessage("dashboard.responsive.md", md.getValue().intValue()))
        );
        lg.addValueChangeListener(event ->
                lg.setCaption(formatMessage("dashboard.responsive.lg", lg.getValue().intValue()))
        );

        xs.focus();

        Layout dbox = desktopBox.unwrap(Layout.class);
        Layout mbox = mobileBox.unwrap(Layout.class);
        mbox.addComponent(xs);
        mbox.addComponent(md);
        dbox.addComponent(sm);
        dbox.addComponent(lg);
    }

    public void apply() {
        this.close(new StandardCloseAction(COMMIT_ACTION_ID));
    }

    public void cancel() {
        this.close(new StandardCloseAction(CLOSE_ACTION_ID));
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