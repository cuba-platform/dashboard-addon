/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.drop_handlers;

import com.haulmont.addon.dnd.components.DropHandler;
import com.haulmont.addon.dnd.components.acceptcriterion.AcceptCriterion;
import com.haulmont.addon.dnd.components.dragevent.DragAndDropEvent;
import com.haulmont.addon.dnd.web.gui.components.AcceptCriterionWrapper;
import com.vaadin.server.PaintTarget;
import org.springframework.context.annotation.Scope;

@org.springframework.stereotype.Component
@Scope("prototype")
public class NotDropHandler implements DropHandler {
    @Override
    public void drop(DragAndDropEvent event) {

    }

    @Override
    public AcceptCriterion getCriterion() {
        return (AcceptCriterionWrapper) () -> new com.vaadin.event.dd.acceptcriteria.AcceptCriterion() {
            @Override
            public boolean isClientSideVerifiable() {
                return true;
            }

            @Override
            public void paint(PaintTarget target) {

            }

            @Override
            public void paintResponse(PaintTarget target) {

            }

            @Override
            public boolean accept(com.vaadin.event.dd.DragAndDropEvent dragEvent) {
                return false;
            }
        };
    }
}
