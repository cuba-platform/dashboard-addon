/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.model.widget_types;

import com.audimex.dashboard.model.Widget;
import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;

@MetaClass(name = "amxd$ScreenWidget")
@WidgetType(caption = "Screen",
        browseClass = "com.audimex.dashboard.web.widget_types.screen.ScreenWidgetBrowse",
        editClass = "com.audimex.dashboard.web.widget_types.screen.ScreenWidgetEdit")
public class ScreenWidget extends Widget {
    @MetaProperty
    protected String frameId;

    public String getFrameId() {
        return frameId;
    }

    public void setFrameId(String frameId) {
        this.frameId = frameId;
    }
}
