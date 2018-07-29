/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.widget_types.lookup;

import com.haulmont.addon.dashboard.model.widget_types.LookupWidget;
import com.haulmont.addon.dashboard.web.events.WidgetEntitiesSelectedEvent;
import com.haulmont.addon.dashboard.web.events.WidgetEntitiesSelectedEvent.WidgetWithEntities;
import com.haulmont.addon.dashboard.web.widget_types.AbstractWidgetBrowse;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.addon.dashboard.web.events.WidgetEntitiesSelectedEvent;

import javax.inject.Inject;
import java.util.Map;

public class LookupWidgetBrowse extends AbstractWidgetBrowse {
    @Inject
    protected Events events;

    protected AbstractLookup lookupFrame;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        refresh();
    }

    @Override
    public void refresh() {
        String lookupWindowId = ((LookupWidget) widget).getLookupWindowId();
        lookupFrame = openLookup(lookupWindowId, lookupHandler(), WindowManager.OpenType.DIALOG, getParamsForFrame());
        lookupFrame.close("");
        this.add(lookupFrame.getFrame());
    }

    @Override
    public void refresh(Map<String, Object> params) {
        String lookupWindowId = ((LookupWidget) widget).getLookupWindowId();
        lookupFrame = openLookup(lookupWindowId, lookupHandler(), WindowManager.OpenType.DIALOG,  getParamsForFrame(params));
        lookupFrame.close("");
        this.add(lookupFrame.getFrame());
    }

    protected Window.Lookup.Handler lookupHandler() {
        return items -> events.publish(new WidgetEntitiesSelectedEvent(new WidgetEntitiesSelectedEvent.WidgetWithEntities(widget, items)));
    }
}
