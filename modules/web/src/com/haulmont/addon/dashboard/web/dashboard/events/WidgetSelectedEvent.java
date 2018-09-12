/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.events;

import java.util.UUID;

public class WidgetSelectedEvent extends AbstractDashboardEditEvent {

    public enum Target {
        CANVAS,
        TREE;
    }

    private Target target;

    public WidgetSelectedEvent(UUID source) {
        super(source);
    }

    public WidgetSelectedEvent(UUID layoutUuid, Target target) {
        super(layoutUuid);
        this.target = target;
    }

    @Override
    public UUID getSource() {
        return (UUID) super.getSource();
    }

    public Target getTarget() {
        return target;
    }
}
