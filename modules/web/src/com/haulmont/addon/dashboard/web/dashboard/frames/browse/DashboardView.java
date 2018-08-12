/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.haulmont.addon.dashboard.web.dashboard.frames.browse;

import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.web.dashboard.frames.ui_component.WebDashboardFrame;
import com.haulmont.addon.dashboard.web.dashboard.helper.AbstractDashboardViewExtension;
import com.haulmont.addon.dashboard.web.events.DashboardEvent;
import com.haulmont.addon.dashboard.web.events.DashboardUpdatedEvent;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Timer;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.springframework.context.event.EventListener;
import org.springframework.util.ReflectionUtils;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardView extends AbstractWindow {

    public static final String SCREEN_NAME = "dashboard-view";
    public static final String REFERENCE_NAME = "REFERENCE_NAME";

    @Inject
    protected ComponentsFactory componentsFactory;

    @Inject
    protected Events events;

    @Inject
    protected AbstractDashboardViewExtension dashboardViewExtension;

    @Override
    public void init(Map<String, Object> params) {
        WebDashboardFrame frame = (WebDashboardFrame) openFrame(null, WebDashboardFrame.SCREEN_NAME);
        frame.setReferenceName((String) params.getOrDefault(REFERENCE_NAME, ""));
        this.add(frame);
        frame.refresh();
        initTimer(frame);
        if (dashboardViewExtension != null) {
            dashboardViewExtension.setWebDashboardFrame(frame);
        }
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

    @SuppressWarnings("unchecked")
    @EventListener
    public void dashboardEventListener(DashboardEvent dashboardEvent) throws InvocationTargetException, IllegalAccessException {
        if (dashboardViewExtension == null) {
            return;
        }
        Class eventClass = dashboardEvent.getClass();
        Method[] methods = ReflectionUtils.getUniqueDeclaredMethods(dashboardViewExtension.getClass());
        List<Method> eventListenerMethods = Arrays.stream(methods)
                .filter(m -> m.getAnnotation(EventListener.class) != null)
                .filter(m -> m.getParameterCount() == 1)
                .collect(Collectors.toList());

        for (Method method : eventListenerMethods) {
            Parameter[] parameters = method.getParameters();
            Parameter parameter = parameters[0];
            Class methodEventTypeArg = parameter.getType();
            if (methodEventTypeArg.isAssignableFrom(eventClass)) {
                method.invoke(dashboardViewExtension, dashboardEvent);

            }
        }


    }

}