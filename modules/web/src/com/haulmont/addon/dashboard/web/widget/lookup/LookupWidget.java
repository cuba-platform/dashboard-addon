/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.widget.lookup;

import com.haulmont.addon.dashboard.gui.components.DashboardFrame;
import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.ParameterType;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.web.annotation.WidgetParam;
import com.haulmont.addon.dashboard.web.annotation.DashboardWidget;
import com.haulmont.addon.dashboard.web.repository.WidgetRepository;
import com.haulmont.addon.dashboard.web.events.DashboardEvent;
import com.haulmont.addon.dashboard.web.events.ItemsSelectedEvent;
import com.haulmont.addon.dashboard.web.widget.RefreshableWidget;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.haulmont.addon.dashboard.web.widget.lookup.LookupWidget.CAPTION;


@DashboardWidget(name = CAPTION, editFrameId = "dashboard$LookupWidgetEdit")
public class LookupWidget extends AbstractFrame implements RefreshableWidget {

    public static final String CAPTION = "Lookup";

    @Inject
    protected Events events;

    @Inject
    protected WidgetRepository widgetRepository;

    @WindowParam
    protected Widget widget;

    @WindowParam
    protected Dashboard dashboard;

    @WindowParam
    protected DashboardFrame dashboardFrame;

    protected AbstractWindow lookupFrame;

    @WidgetParam(type = ParameterType.STRING)
    @WindowParam
    protected String lookupWindowId;

    @Override
    public void init(Map<String, Object> params) {
        lookupFrame = openWindow(lookupWindowId, WindowManager.OpenType.DIALOG, widgetRepository.getWidgetParams(widget));
        List<Table> tables = findTables(lookupFrame);
        for (Table table : tables) {
            table.getDatasource().addItemChangeListener(e -> events.publish(new ItemsSelectedEvent(widget, table.getSelected())));
        }
        lookupFrame.close(Window.CLOSE_ACTION_ID);
        this.add(lookupFrame.getFrame());
    }

    protected List<Table> findTables(AbstractFrame abstractFrame) {
        return abstractFrame.getComponents().stream()
                .filter(c -> c instanceof Table)
                .map(c -> (Table) c)
                .collect(Collectors.toList());
    }

    @Override
    public void refresh(DashboardEvent dashboardEvent) {
        lookupFrame.getDsContext().refresh();
    }

}
