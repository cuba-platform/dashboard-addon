/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.widget_types.lookup;

import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.ParameterType;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.web.annotation.WidgetParam;
import com.haulmont.addon.dashboard.web.annotation.WidgetType;
import com.haulmont.addon.dashboard.web.annotation_analyzer.WidgetRepository;
import com.haulmont.addon.dashboard.web.events.DashboardEvent;
import com.haulmont.addon.dashboard.web.events.WidgetEntitiesSelectedEvent;
import com.haulmont.addon.dashboard.web.widget_types.RefreshableWidget;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Window;

import javax.inject.Inject;
import java.util.Map;

import static com.haulmont.addon.dashboard.web.widget_types.lookup.LookupWidgetBrowse.CAPTION;


@WidgetType(name = CAPTION, editFrameId = "lookupWidgetEdit")
public class LookupWidgetBrowse extends AbstractFrame implements RefreshableWidget {

    public static final String CAPTION = "Lookup";

    @Inject
    protected Events events;

    @Inject
    protected WidgetRepository widgetRepository;

    @WindowParam
    protected Widget widget;

    @WindowParam
    protected Dashboard dashboard;

    protected AbstractLookup lookupFrame;

    @WidgetParam(type = ParameterType.STRING)
    @WindowParam
    protected String lookupWindowId;

    @Override
    public void init(Map<String, Object> params) {
        lookupFrame = openLookup(lookupWindowId, lookupHandler(), WindowManager.OpenType.DIALOG, widgetRepository.getWidgetParams(widget));
        lookupFrame.close("");
        this.add(lookupFrame.getFrame());
    }

    protected Window.Lookup.Handler lookupHandler() {
        return items -> events.publish(new WidgetEntitiesSelectedEvent(new WidgetEntitiesSelectedEvent.WidgetWithEntities(widget, items)));
    }

    @Override
    public void refresh(DashboardEvent dashboardEvent) {
        lookupFrame.getDsContext().refresh();
    }

}
