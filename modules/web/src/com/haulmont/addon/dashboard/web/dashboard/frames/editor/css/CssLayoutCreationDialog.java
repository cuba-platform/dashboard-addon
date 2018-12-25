/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
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