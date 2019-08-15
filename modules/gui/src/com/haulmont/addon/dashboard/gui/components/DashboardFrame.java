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

package com.haulmont.addon.dashboard.gui.components;

import com.haulmont.addon.dashboard.model.Dashboard;
import com.haulmont.addon.dashboard.model.Parameter;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.screen.ScreenFragment;

import java.util.List;
import java.util.Map;

/**
 * Represents in a frame  a dashboard {@link Dashboard}
 * Dashboard can be configured by {@link Dashboard#setCode(String)} or by the classpath of dashboard.json
 * It is possible to add (or rewrite) primitive parameters in the dashboard.
 */
public interface DashboardFrame extends Frame {

    String NAME = "dashboard";

    /**
     * Initialize dashboard with passed parameters, timer and assistant.
     *
     * @param params map of dashboard params
     */
    void init(Map<String, Object> params);

    /**
     * Returns dashboard entity
     *
     * @return dashboard entity
     */
    Dashboard getDashboard();

    /**
     * Returns widget by passed id
     *
     * @param widgetId widget identifier
     */
    ScreenFragment getWidget(String widgetId);

    /**
     * Refreshes dashboard.
     * Dashboard will be refreshed .
     * If existed parameter has the same name as one of the param from passed map, it will be overwritten by param from map.
     */
    void refresh();

    /**
     * Refreshes dashboard with passed parameters.
     * Dashboard will be refreshed with merged existed and new parameters.
     * If existed parameter has the same name as one of the param from passed map, it will be overwritten by param from map.
     *
     * @param params map with new dashboard parameters
     */
    void refresh(Map<String, Object> params);

    /**
     * Dashboard can be configured by setting code of existing dashboard
     * To apply new changes the {@link DashboardFrame#init(Map)} method required to be invoked
     *
     * @param code another dashboard code
     */
    void setCode(String code);

    /**
     * Dashboard can be configured from json file
     *
     * @param jsonPath path to json configuration file
     */
    void setJsonPath(String jsonPath);

    /**
     * Sets passed parameters to dashboard frame
     *
     * @param parameters list of parameters to be set
     */
    void setXmlParameters(List<Parameter> parameters);

    /**
     * Set delay for dashboard timer
     *
     * @param delay delay time in seconds
     */
    void setTimerDelay(int delay);

    /**
     * Returns delay for timer
     *
     * @return delay time in seconds
     */
    int getTimerDelay();

    /**
     * Returns assistant bean name
     *
     * @return name of assistant bean
     */
    String getAssistantBeanName();

    /**
     * Set assistant bean name
     *
     * @param assistantBeanName name of the assistant bean
     */
    void setAssistantBeanName(String assistantBeanName);
}
