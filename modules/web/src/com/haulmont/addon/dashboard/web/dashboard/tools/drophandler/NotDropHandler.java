/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.tools.drophandler;


import com.haulmont.addon.dnd.components.DropHandler;
import com.haulmont.addon.dnd.components.acceptcriterion.AcceptCriterion;
import com.haulmont.addon.dnd.components.dragevent.DragAndDropEvent;
import com.haulmont.addon.dnd.web.gui.components.AcceptCriterionWrapper;
import com.vaadin.event.dd.acceptcriteria.ServerSideCriterion;

public class NotDropHandler implements DropHandler {

    @Override
    public void drop(DragAndDropEvent event) {
        //do nothing
    }

    @Override
    public AcceptCriterion getCriterion() {
        return (AcceptCriterionWrapper) () -> new ServerSideCriterion() {
            @Override
            public boolean accept(com.vaadin.event.dd.DragAndDropEvent dragEvent) {
                return false;
            }
        };
    }
}
