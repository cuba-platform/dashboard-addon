/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.haulmont.addon.dashboard.web.dashboard.frames.editor.css;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.CheckBox;
import com.haulmont.cuba.gui.components.TextField;

import javax.inject.Inject;
import java.util.Map;

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
        this.close(COMMIT_ACTION_ID);
    }

    public void cancel() {
        this.close(CLOSE_ACTION_ID);
    }

    public String getCssStyleName() {
        return styleName.getValue();
    }

    public Boolean getResponsive() {
        return responsive.getValue();
    }

}