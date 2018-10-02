package com.haulmont.addon.dashboard.web.dashboard.frames.browse;

import com.haulmont.addon.dashboard.entity.PersistentDashboard;
import com.haulmont.addon.dashboard.web.dashboard.frames.view.DashboardView;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.GroupDatasource;

import javax.inject.Inject;
import java.util.UUID;

import static com.haulmont.cuba.gui.WindowManager.OpenType.NEW_WINDOW;

public class PersistentDashboardBrowse extends AbstractLookup {
    public void viewDashboard() {
        PersistentDashboard item = persistentDashboardsDs.getItem();
        if (item != null) {
            openWindow("dashboard$PersistentDashboard.view", NEW_WINDOW,
                    ParamsMap.of(
                            DashboardView.REFERENCE_NAME, item.getReference(),
                            DashboardView.DISPLAY_NAME, item.getName()));
        }

    }

    @Inject
    protected GroupDatasource<PersistentDashboard, UUID> persistentDashboardsDs;
}
