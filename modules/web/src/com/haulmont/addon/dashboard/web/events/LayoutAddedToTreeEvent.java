package com.haulmont.addon.dashboard.web.events;

import com.haulmont.addon.dashboard.model.visual_model.DashboardLayout;
import java.util.UUID;

public class LayoutAddedToTreeEvent extends DashboardEvent {

    public LayoutAddedToTreeEvent(LayoutAddedToTree source) {
        super(source);
    }

    @Override
    public LayoutAddedToTreeEvent.LayoutAddedToTree getSource() {
        return (LayoutAddedToTreeEvent.LayoutAddedToTree) super.getSource();
    }

    public static class LayoutAddedToTree {
        private UUID parentLayoutUuid;
        private DashboardLayout layoutType;

        public LayoutAddedToTree(UUID parentLayoutUuid, DashboardLayout layoutType) {
            this.parentLayoutUuid = parentLayoutUuid;
            this.layoutType = layoutType;
        }

        public UUID getParentLayoutUuid() {
            return parentLayoutUuid;
        }

        public DashboardLayout getLayoutType() {
            return layoutType;
        }
    }
}
