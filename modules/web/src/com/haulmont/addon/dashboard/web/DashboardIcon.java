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

package com.haulmont.addon.dashboard.web;

import com.haulmont.cuba.gui.icons.Icons;

public enum DashboardIcon implements Icons.Icon {
    VERTICAL_LAYOUT_ICON("icons/vertical-container.png"),
    HORIZONTAL_LAYOUT_ICON("icons/horizontal-container.png"),
    GRID_LAYOUT_ICON("icons/grid-container.png"),
    CSS_LAYOUT_ICON("icons/css-container.png"),
    TRASH_ICON("icons/trash.png"),
    GEAR_ICON("icons/gear.png");

    protected String source;

    DashboardIcon(String source) {
        this.source = source;
    }

    @Override
    public String source() {
        return source;
    }
}
