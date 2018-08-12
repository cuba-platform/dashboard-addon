package com.haulmont.addon.dashboard.web.dashboard.helper;

import com.haulmont.addon.dashboard.web.dashboard.frames.ui_component.WebDashboardFrame;

public abstract class AbstractDashboardViewExtension {//TODO: add methods

    protected WebDashboardFrame webDashboardFrame;

    public WebDashboardFrame getWebDashboardFrame() {
        return webDashboardFrame;
    }

    public void setWebDashboardFrame(WebDashboardFrame webDashboardFrame) {
        this.webDashboardFrame = webDashboardFrame;
    }
}
