/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.haulmont.addon.dashboard.web.dashboard.frames.editor.expand;

import com.haulmont.addon.dashboard.model.visualmodel.DashboardLayout;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.screen.StandardCloseAction;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

@UiController("dashboard$ExpandDialog")
@UiDescriptor("expand-dialog.xml")
public class ExpandDialog extends AbstractWindow {
    public static final String WIDGET = "WIDGET";

    @Inject
    private LookupField<DashboardLayout> expandLookupField;

    @Inject
    private CollectionDatasource<DashboardLayout, UUID> layoutsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        DashboardLayout layout = (DashboardLayout) params.get(WIDGET);

        if (layout == null) {
            close(new StandardCloseAction(CLOSE_ACTION_ID));
            return;
        }

        UUID expandedLayout = layout.getExpand();

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
        this.close(new StandardCloseAction(COMMIT_ACTION_ID));
    }

    public void cancel() {
        this.close(new StandardCloseAction(CLOSE_ACTION_ID));
    }
}