/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.gui.components;

import com.haulmont.bali.datastruct.Pair;
import com.haulmont.cuba.gui.components.Frame;

import java.util.List;
import java.util.UUID;

public interface DashboardFrame extends Frame {

    String NAME = "amdxDashboardComponent";

    UUID getDashboardId();

    void setDashboardId(UUID dashboardId);

    String getJsonPath();

    void setJsonPath(String jsonPath);

    List<Pair<String, String>> getParameters();

    void setParameters(List<Pair<String, String>> parameters);

    void refresh();

}
