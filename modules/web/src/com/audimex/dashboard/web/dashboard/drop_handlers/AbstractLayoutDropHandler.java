/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.drop_handlers;

import com.audimex.dashboard.web.dashboard.tools.DropLayoutTool;
import com.haulmont.addon.dnd.components.DropHandler;
import com.haulmont.addon.dnd.components.acceptcriterion.AcceptCriterion;

public abstract class AbstractLayoutDropHandler implements DropHandler {
    protected DropLayoutTool tool;

    public AbstractLayoutDropHandler(DropLayoutTool tool) {
        this.tool = tool;
    }

    public DropLayoutTool getTool() {
        return tool;
    }

    public void setTool(DropLayoutTool tool) {
        this.tool = tool;
    }

    @Override
    public AcceptCriterion getCriterion() {
        return AcceptCriterion.ACCEPT_ALL;
    }
}
