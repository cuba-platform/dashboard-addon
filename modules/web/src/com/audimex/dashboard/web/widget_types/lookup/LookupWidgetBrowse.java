/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.widget_types.lookup;

import com.audimex.dashboard.model.widget_types.LookupWidget;
import com.audimex.dashboard.web.widget_types.AbstractWidgetBrowse;
import com.haulmont.cuba.gui.components.AbstractLookup;

import java.util.Map;

public class LookupWidgetBrowse extends AbstractWidgetBrowse {
    protected AbstractLookup lookupFrame;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        String lookupWindowId = ((LookupWidget) widget).getLookupWindowId();
        lookupFrame = (AbstractLookup) openFrame(this, lookupWindowId, getParamsForFrame());
        lookupFrame.setSizeFull();
        //todo: DASH-26 add fire itemsSelected event for browse
    }
}
