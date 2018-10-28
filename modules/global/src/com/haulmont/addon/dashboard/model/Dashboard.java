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
package com.haulmont.addon.dashboard.model;

import com.haulmont.addon.dashboard.model.visualmodel.*;
import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@NamePattern("%s|title")
@MetaClass(name = "dashboard$Dashboard")
public class Dashboard extends BaseUuidEntity {
    @NotNull
    @MetaProperty(mandatory = true)
    protected String title;

    /**
     * The unique identifier for searching in a database.
     */
    @NotNull
    @MetaProperty(mandatory = true)
    protected String code;

    /**
     * Stores a hierarchy of a visual model
     */
    @MetaProperty
    protected RootLayout visualModel;
    @MetaProperty
    protected List<Parameter> parameters = new ArrayList<>();
    @MetaProperty
    protected Boolean isAvailableForAllUsers = true;

    /**
     * Stores a login of the user, who created entity
     */
    @MetaProperty
    protected String createdBy;

    /**
     * Stores delay for publishing DashboardUpdatedEvent in view mode
     */
    @MetaProperty
    protected Integer timerDelay = 0;

    /**
     * Stores assistance bean name for dashboard
     */
    @MetaProperty
    protected String assistantBeanName;

    public Boolean getIsAvailableForAllUsers() {
        return isAvailableForAllUsers;
    }

    public void setIsAvailableForAllUsers(Boolean availableForAllUsers) {
        isAvailableForAllUsers = availableForAllUsers;
    }

    public List<Widget> getWidgets() {
        List<Widget> widgets = new ArrayList<>();
        getWidgets(visualModel, widgets);
        return widgets;
    }

    private void getWidgets(DashboardLayout dashboardLayout, List<Widget> widgets) {
        if (dashboardLayout == null) {
            return;
        }
        if (WidgetLayout.class.isAssignableFrom(dashboardLayout.getClass())) {
            widgets.add(((WidgetLayout) dashboardLayout).getWidget());
        }
        if (GridLayout.class.isAssignableFrom(dashboardLayout.getClass())) {
            for (GridArea gridArea : ((GridLayout) dashboardLayout).getAreas()) {
                DashboardLayout component = gridArea.getComponent();
                getWidgets(component, widgets);

            }
        } else {
            for (DashboardLayout child : dashboardLayout.getChildren()) {
                getWidgets(child, widgets);

            }
        }

    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public RootLayout getVisualModel() {
        return visualModel;
    }

    public void setVisualModel(RootLayout visualModel) {
        this.visualModel = visualModel;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public Integer getTimerDelay() {
        return timerDelay;
    }

    public void setTimerDelay(Integer timerDelay) {
        this.timerDelay = timerDelay;
    }

    public String getAssistantBeanName() {
        return assistantBeanName;
    }

    public void setAssistantBeanName(String assistantBeanName) {
        this.assistantBeanName = assistantBeanName;
    }
}