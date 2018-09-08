package com.haulmont.addon.dashboard.web.dto;

import com.haulmont.addon.dashboard.model.visualmodel.DashboardLayout;

import java.util.UUID;

public class LayoutRemoveDto {
    private final DashboardLayout dashboardLayout;
    private final UUID removedUuid;
    private final UUID selectUuid;

    public LayoutRemoveDto(DashboardLayout dashboardLayout, UUID removedUuid, UUID selectUuid) {
        this.dashboardLayout = dashboardLayout;
        this.removedUuid = removedUuid;
        this.selectUuid = selectUuid;
    }

    public DashboardLayout getDashboardLayout() {
        return dashboardLayout;
    }

    public UUID getRemovedUuid() {
        return removedUuid;
    }

    public UUID getSelectUuid() {
        return selectUuid;
    }
}
