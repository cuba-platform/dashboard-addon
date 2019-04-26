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
package com.haulmont.addon.dashboard.web.dashboard.frames.editor.css;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.CheckBox;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.screen.StandardCloseAction;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

import javax.inject.Inject;
import java.util.Map;

@UiController("dashboard$CssLayoutDialog")
@UiDescriptor("css-creation-dialog.xml")
public class CssLayoutCreationDialog extends AbstractWindow {
    public static final String SCREEN_NAME = "dashboard$CssLayoutDialog";

    @Inject
    private CheckBox responsive;
    @Inject
    private TextField<String> styleName;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    public void apply() {
        close(new StandardCloseAction(COMMIT_ACTION_ID));
    }

    public void cancel() {
        close(new StandardCloseAction(CLOSE_ACTION_ID));
    }

    public String getCssStyleName() {
        return styleName.getValue();
    }

    public Boolean getResponsive() {
        return responsive.getValue();
    }

}