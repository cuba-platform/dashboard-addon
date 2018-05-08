/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.widget_types.lookup;

import com.audimex.dashboard.model.widget_types.LookupWidget;
import com.audimex.dashboard.web.widget_types.AbstractWidgetBrowse;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.VBoxLayout;

import javax.inject.Inject;
import java.util.Map;

public class LookupWidgetBrowse extends AbstractWidgetBrowse {
    @Inject
    protected VBoxLayout lookupBox;

    protected AbstractLookup lookupFrame;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        String lookupWindowId = ((LookupWidget) widget).getLookupWindowId();
        lookupFrame = (AbstractLookup) openFrame(lookupBox, lookupWindowId, getParamsForFrame());
        //todo: DASH-26 add fire itemsSelected event for browse
    }
}
