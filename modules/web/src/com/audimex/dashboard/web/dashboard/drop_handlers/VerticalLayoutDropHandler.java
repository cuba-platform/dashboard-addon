/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.drop_handlers;

import com.audimex.dashboard.web.dashboard.tools.DropLayoutTools;
import fi.jasoft.dragdroplayouts.drophandlers.DefaultVerticalLayoutDropHandler;

public class VerticalLayoutDropHandler extends DefaultVerticalLayoutDropHandler {

    protected DropLayoutTools tools;

    public VerticalLayoutDropHandler(DropLayoutTools tools) {
        this.tools = tools;
    }
}
