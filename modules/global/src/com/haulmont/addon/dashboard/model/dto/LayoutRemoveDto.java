package com.haulmont.addon.dashboard.model.dto;

import com.haulmont.addon.dashboard.model.visual_model.DashboardLayout;

import java.util.UUID;

public class LayoutRemoveDto {
    private final DashboardLayout dashboardLayout;
    private final UUID removedUuid;

    public LayoutRemoveDto(DashboardLayout dashboardLayout, UUID removedUuid) {
        this.dashboardLayout = dashboardLayout;
        this.removedUuid = removedUuid;
    }

    public DashboardLayout getDashboardLayout() {
        return dashboardLayout;
    }

    public UUID getRemovedUuid() {
        return removedUuid;
    }
}
