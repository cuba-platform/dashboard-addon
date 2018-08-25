/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.haulmont.addon.dashboard.web.dashboard.frames.browse;

import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.web.dashboard.frames.ui_component.WebDashboardFrame;
import com.haulmont.addon.dashboard.web.dashboard.assistant.DashboardViewAssistant;
import com.haulmont.addon.dashboard.web.events.DashboardEvent;
import com.haulmont.addon.dashboard.web.events.DashboardUpdatedEvent;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Timer;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.AbstractApplicationContext;
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

    protected DashboardViewAssistant dashboardViewAssistant;

    @Override
    public void init(Map<String, Object> params) {
        WebDashboardFrame frame = (WebDashboardFrame) openFrame(null, WebDashboardFrame.SCREEN_NAME);
        frame.setReferenceName((String) params.getOrDefault(REFERENCE_NAME, ""));
        this.add(frame);
        frame.refresh();
        initTimer(frame);
        initAssistant(frame);
    }

    private void initAssistant(WebDashboardFrame frame) {
        Dashboard dashboard = frame.getDashboard();
        String assistantBeanName = StringUtils.isEmpty(frame.getAssistantBeanName()) ? dashboard.getAssistantBeanName() : frame.getAssistantBeanName();
        if (StringUtils.isNotEmpty(assistantBeanName)) {
            DashboardViewAssistant assistantBean = AppBeans.get(assistantBeanName, DashboardViewAssistant.class);
            BeanFactory bf = ((AbstractApplicationContext) AppContext.getApplicationContext()).getBeanFactory();
            if (assistantBean != null && bf.isPrototype(assistantBeanName)) {
                assistantBean.init(frame);
                dashboardViewAssistant = assistantBean;
            }
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
        if (dashboardViewAssistant == null) {
            return;
        }
        Class eventClass = dashboardEvent.getClass();
        Method[] methods = ReflectionUtils.getUniqueDeclaredMethods(dashboardViewAssistant.getClass());
        List<Method> eventListenerMethods = Arrays.stream(methods)
                .filter(m -> m.getAnnotation(EventListener.class) != null)
                .filter(m -> m.getParameterCount() == 1)
                .collect(Collectors.toList());

        for (Method method : eventListenerMethods) {
            Parameter[] parameters = method.getParameters();
            Parameter parameter = parameters[0];
            Class methodEventTypeArg = parameter.getType();
            if (methodEventTypeArg.isAssignableFrom(eventClass)) {
                method.invoke(dashboardViewAssistant, dashboardEvent);

            }
        }


    }

}