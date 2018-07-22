/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.dashboard.frames.browse;

import com.audimex.dashboard.model.Dashboard;
import com.audimex.dashboard.web.dashboard.frames.ui_component.WebDashboardFrame;
import com.audimex.dashboard.web.events.DashboardUpdatedEvent;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Timer;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.springframework.context.event.EventListener;

import javax.inject.Inject;
import java.util.Map;

public class DashboardView extends AbstractWindow {

    public static final String SCREEN_NAME = "dashboard-view";
    public static final String REFERENCE_NAME = "REFERENCE_NAME";

    @Inject
    protected ComponentsFactory componentsFactory;

    @Inject
    protected Events events;

    @Override
    public void init(Map<String, Object> params) {
        WebDashboardFrame frame = (WebDashboardFrame) openFrame(null, WebDashboardFrame.SCREEN_NAME);
        frame.setReferenceName((String) params.getOrDefault(REFERENCE_NAME, ""));
        this.add(frame);
        frame.refresh();
        initTimer(frame);
    }

    private void initTimer(WebDashboardFrame frame) {
        Dashboard dashboard = frame.getDashboard();
        int timerDelay = frame.getTimerDelay() == 0 ? dashboard.getTimerDelay() : frame.getTimerDelay();
        if (timerDelay > 0) {
            Timer dashboardUpdatedTimer = componentsFactory.createTimer();
            addTimer(dashboardUpdatedTimer);
            dashboardUpdatedTimer.setDelay(timerDelay);
            dashboardUpdatedTimer.setRepeating(true);
            dashboardUpdatedTimer.addActionListener(timer -> events.publish(new DashboardUpdatedEvent(dashboard)));
            dashboardUpdatedTimer.start();
        }
    }

}