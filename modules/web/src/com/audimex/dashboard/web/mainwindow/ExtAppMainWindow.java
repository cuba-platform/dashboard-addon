/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.mainwindow;

import com.haulmont.cuba.web.app.mainwindow.AppMainWindow;
import com.haulmont.cuba.web.gui.components.mainwindow.WebAppWorkArea;
import com.haulmont.cuba.web.toolkit.ui.CubaTabSheet;
import com.vaadin.event.dd.DragAndDropEvent;
import fi.jasoft.dragdroplayouts.drophandlers.DefaultTabSheetDropHandler;

import java.util.Map;

public class ExtAppMainWindow extends AppMainWindow {
    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        CubaTabSheet tabbedWindowContainer = ((WebAppWorkArea) workArea).getTabbedWindowContainer();
        tabbedWindowContainer.setDropHandler(new DefaultTabSheetDropHandler() {
            @Override
            protected void handleDropFromLayout(DragAndDropEvent event) {
                // disabled
            }

            @Override
            protected void handleDropFromAbsoluteParentLayout(DragAndDropEvent event) {
                // disabled
            }
        });
    }
}