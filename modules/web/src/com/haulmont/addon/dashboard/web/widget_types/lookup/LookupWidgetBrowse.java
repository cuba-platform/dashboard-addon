/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.widget_types.lookup;

import com.haulmont.addon.dashboard.model.ParameterType;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.web.annotation.WidgetParam;
import com.haulmont.addon.dashboard.web.annotation.WidgetType;
import com.haulmont.addon.dashboard.web.events.WidgetEntitiesSelectedEvent;
import com.haulmont.addon.dashboard.web.events.WidgetUpdatedEvent;
import com.haulmont.addon.dashboard.web.widget_types.AbstractWidgetBrowse;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Window;

import javax.inject.Inject;
import java.util.Map;

import static com.haulmont.addon.dashboard.web.widget_types.lookup.LookupWidgetBrowse.CAPTION;


@WidgetType(name = CAPTION, editFrameId = "lookupWidgetEdit")
public class LookupWidgetBrowse extends AbstractWidgetBrowse {

    public static final String CAPTION = "Lookup";

    @Inject
    protected Events events;

    protected AbstractLookup lookupFrame;

    @WidgetParam(type = ParameterType.STRING)
    protected String lookupWindowId;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        refresh();
    }

    @Override
    public void refresh() {
        lookupFrame = openLookup(lookupWindowId, lookupHandler(), WindowManager.OpenType.DIALOG, getParamsForFrame());
        lookupFrame.close("");
        this.add(lookupFrame.getFrame());
    }

    @Override
    public void refresh(Map<String, Object> params) {
        lookupFrame = openLookup(lookupWindowId, lookupHandler(), WindowManager.OpenType.DIALOG,  getParamsForFrame(params));
        lookupFrame.close("");
        this.add(lookupFrame.getFrame());
    }

    protected Window.Lookup.Handler lookupHandler() {
        return items -> events.publish(new WidgetEntitiesSelectedEvent(new WidgetEntitiesSelectedEvent.WidgetWithEntities(widget, items)));
    }

    /*public void onFireEventClick() {
        events.publish(new WidgetUpdatedEvent(new Widget()));
    }*/
}
