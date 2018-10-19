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
package com.haulmont.addon.dashboard.web.dashboard.frames.view;

import com.haulmont.addon.dashboard.web.dashboard.frames.uicomponent.WebDashboardFrame;
import com.haulmont.cuba.gui.components.AbstractWindow;

import java.util.Map;

public class DashboardView extends AbstractWindow {

    public static final String SCREEN_NAME = "dashboard$DashboardView";
    public static final String CODE = "CODE";
    public static final String DISPLAY_NAME = "DISPLAY_NAME";

    @Override
    public void init(Map<String, Object> params) {
        if (params.containsKey(DISPLAY_NAME)){
            setCaption((String) params.get(DISPLAY_NAME));
        }
        openFrame(this, WebDashboardFrame.SCREEN_NAME, params);
    }


}