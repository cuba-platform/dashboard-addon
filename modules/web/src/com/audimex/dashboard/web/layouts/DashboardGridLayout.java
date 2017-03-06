package com.audimex.dashboard.web.layouts;

import fi.jasoft.dragdroplayouts.DDGridLayout;

public class DashboardGridLayout extends DDGridLayout implements HasDragCaption {
    protected String caption;
    protected String icon;

    @Override
    public String getWidgetIcon() {
        return icon;
    }

    public void setWidgetIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String getWidgetCaption() {
        return caption;
    }

    @Override
    public void setWidgetCaption(String caption) {
        this.caption = caption;
    }
}
