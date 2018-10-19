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

package com.haulmont.addon.dashboard.gui.components;

import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.Parameter;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Frame;

import java.util.List;

/**
 * Represents in a frame  a dashboard {@link Dashboard}
 * Dashboard can be configured by {@link Dashboard#code} or by the classpath of dashboard.json
 * It is possible to add (or rewrite) primitive parameters in the dashboard.
 */
public interface DashboardFrame extends Frame {

    void setCode(String code);

    void setJsonPath(String jsonPath);

    void setXmlParameters(List<Parameter> parameters);

    void setTimerDelay(int delay);

    int getTimerDelay();

    Dashboard getDashboard();

    void refresh();

    String getAssistantBeanName();

    void setAssistantBeanName(String assistantBeanName);

    AbstractFrame getWidget(String widgetId);

}
