package com.haulmont.addon.dashboard.web.dashboard.helper;

import com.haulmont.addon.dashboard.gui.components.WidgetBrowse;
import com.haulmont.addon.dashboard.web.dashboard.frames.ui_component.WebDashboardFrame;
import com.haulmont.addon.dashboard.web.events.WidgetUpdatedEvent;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class DashboardViewExtension extends AbstractDashboardViewExtension {

    @Inject
    protected ComponentsFactory componentsFactory;


    @EventListener
    public void dashboardEventListener(WidgetUpdatedEvent event) {
        WebDashboardFrame webDashboardFrame = getWebDashboardFrame();
        WidgetBrowse wb = webDashboardFrame.getWidgetBrowse("234");
        Button button = componentsFactory.createComponent(Button.class);
        button.setCaption("123");
        button.setEnabled(true);
        button.setVisible(true);
        button.setDescription("123");
        wb.add(button);

    }
}
