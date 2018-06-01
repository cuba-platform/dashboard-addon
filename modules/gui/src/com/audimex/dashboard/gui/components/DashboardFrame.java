/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.gui.components;

import com.audimex.dashboard.model.Parameter;
import com.haulmont.cuba.gui.components.Frame;

import java.util.List;

public interface DashboardFrame extends Frame {

    String NAME = "amdxDashboardComponent";

    void setReferenceName(String referenceName);

    void setJsonPath(String jsonPath);

    void setXmlParameters(List<Parameter> parameters);

    void refresh();

}
