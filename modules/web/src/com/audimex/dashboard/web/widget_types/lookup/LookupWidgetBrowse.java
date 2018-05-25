/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.widget_types.lookup;

import com.audimex.dashboard.model.widget_types.LookupWidget;
import com.audimex.dashboard.web.events.WidgetEntitiesSelectedEvent;
import com.audimex.dashboard.web.events.WidgetEntitiesSelectedEvent.WidgetWithEntities;
import com.audimex.dashboard.web.widget_types.AbstractWidgetBrowse;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.LookupComponent;
import com.haulmont.cuba.gui.components.Window;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class LookupWidgetBrowse extends AbstractWidgetBrowse {
    @Inject
    protected Events events;

    protected AbstractLookup lookupFrame;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        String lookupWindowId = ((LookupWidget) widget).getLookupWindowId();
        lookupFrame = openLookup(lookupWindowId, lookupHandler(), WindowManager.OpenType.DIALOG, getParamsForFrame());
        lookupFrame.close("");
        this.add(lookupFrame.getFrame());
    }

    protected Window.Lookup.Handler lookupHandler() {
        return items -> events.publish(new WidgetEntitiesSelectedEvent(new WidgetWithEntities(widget, items)));
    }
}
