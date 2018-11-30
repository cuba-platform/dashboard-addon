/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.haulmont.addon.dashboard.web.dashboard.frames.editor.expand;

import com.haulmont.addon.dashboard.model.visualmodel.DashboardLayout;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class ExpandDialog extends AbstractWindow {
    public static final String SCREEN_NAME = "dashboard$ExpandDialog";
    public static final String LAYOUT = "LAYOUT";
    public static final String EXPAND = "EXPAND";

    @Inject
    private LookupField<DashboardLayout> expandLookupField;

    @Inject
    private CollectionDatasource<DashboardLayout, UUID> layoutsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        DashboardLayout layout = (DashboardLayout) params.get(LAYOUT);

        UUID expandedLayout = (UUID) params.get(EXPAND);

        if (layout == null) {
            //TODO remove deprecaded code
            close(CLOSE_ACTION_ID);
            return;
        }
        for (DashboardLayout child : layout.getChildren()) {
            layoutsDs.addItem(child);
        }

        if (expandedLayout != null) {
            DashboardLayout selectedLayout = layoutsDs.getItem(expandedLayout);
            expandLookupField.setValue(selectedLayout);
        }
    }

    public DashboardLayout getExpand() {
        return expandLookupField.getValue();
    }

    public void apply() {
        this.close(COMMIT_ACTION_ID);
    }

    public void cancel() {
        this.close(CLOSE_ACTION_ID);
    }
}