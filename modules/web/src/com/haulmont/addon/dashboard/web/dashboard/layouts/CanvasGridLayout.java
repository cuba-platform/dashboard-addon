/*
 * Copyright (c) 2008-2018 Haulmont.
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
 *
 */

package com.haulmont.addon.dashboard.web.dashboard.layouts;

import com.haulmont.addon.dashboard.model.visualmodel.GridLayout;
import com.haulmont.addon.dnd.web.gui.components.WebDDGridLayout;
import com.haulmont.cuba.gui.components.Component;

public class CanvasGridLayout extends AbstractCanvasLayout {
    protected WebDDGridLayout gridLayout;

    public CanvasGridLayout(GridLayout model) {
        super(model, new WebDDGridLayout());
        gridLayout = (WebDDGridLayout) delegate;

        gridLayout.setColumns(model.getColumns());
        gridLayout.setRows(model.getRows());
    }

    @Override
    public WebDDGridLayout getDelegate() {
        return gridLayout;
    }

    public void addComponent(Component component, int col, int row, int col2, int row2) {
        gridLayout.add(component, col, row, col2, row2);
    }
}
