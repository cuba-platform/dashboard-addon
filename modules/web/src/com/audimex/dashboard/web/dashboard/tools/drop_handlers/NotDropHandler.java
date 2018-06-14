/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.tools.drop_handlers;


import com.haulmont.addon.dnd.components.DropHandler;
import com.haulmont.addon.dnd.components.acceptcriterion.AcceptCriterion;
import com.haulmont.addon.dnd.components.dragevent.DragAndDropEvent;
import com.haulmont.addon.dnd.web.gui.components.AcceptCriterionWrapper;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;

public class NotDropHandler implements DropHandler {

    @Override
    public void drop(DragAndDropEvent event) {
        //do nothing
    }

    @Override
    public AcceptCriterion getCriterion() {
        return (AcceptCriterionWrapper) AcceptAll::get;
    }
}
