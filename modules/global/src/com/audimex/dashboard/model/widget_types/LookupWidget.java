/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.model.widget_types;

import com.audimex.dashboard.annotation.WidgetType;
import com.audimex.dashboard.model.Widget;
import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;

import static com.audimex.dashboard.model.widget_types.LookupWidget.CAPTION;

/**
 * The LookupWidget is type for screens with a controller inherited from the
 * {@link com.haulmont.cuba.gui.components.AbstractLookup}. Shows screen by {@link ScreenWidget#lookupWindowId}
 */
@MetaClass(name = "amxd$LookupWidget")
@WidgetType(caption = CAPTION,
        browseFrameId = "lookupWidgetBrowse",
        editFrameId = "lookupWidgetEdit")
public class LookupWidget extends Widget {
    public static final String CAPTION = "Lookup";

    @MetaProperty
    protected String lookupWindowId;

    public String getLookupWindowId() {
        return lookupWindowId;
    }

    public void setLookupWindowId(String lookupWindowId) {
        this.lookupWindowId = lookupWindowId;
    }
}
