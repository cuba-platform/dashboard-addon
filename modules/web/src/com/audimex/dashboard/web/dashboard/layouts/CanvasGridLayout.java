/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.layouts;

import com.vaadin.event.LayoutEvents;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import fi.jasoft.dragdroplayouts.DDGridLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;
import fi.jasoft.dragdroplayouts.interfaces.DragFilter;

import java.util.Map;

import static com.audimex.dashboard.web.DashboardStyleConstants.*;

public class CanvasGridLayout extends CssLayout implements CanvasLayout {

    protected DDGridLayout gridLayout = new DDGridLayout();
    protected HorizontalLayout buttonsPanel = new HorizontalLayout();

    public CanvasGridLayout(int cols, int rows) {
        Button configButton = new Button("Config");
        configButton.addStyleName(AMXD_EDIT_BUTTON);

//        Button removeButton = new Button(WebComponentsHelper.getIcon("icons/trash.png"));
        Button removeButton = new Button("Remove");
        removeButton.addStyleName(AMXD_EDIT_BUTTON);

        buttonsPanel.addComponent(configButton);
        buttonsPanel.addComponent(removeButton);
        buttonsPanel.addStyleName(AMXD_LAYOUT_CONTROLS);

        gridLayout.setColumns(cols);
        gridLayout.setRows(rows);
        gridLayout.setDragMode(fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode.CLONE);
        gridLayout.setSizeFull();
        gridLayout.setSpacing(true);
        gridLayout.setMargin(true);
        gridLayout.addStyleName(AMXD_LAYOUT_CONTENT);

        super.addComponent(buttonsPanel);
        super.addComponent(gridLayout);

        this.setDragMode(LayoutDragMode.CLONE);
        this.setSizeFull();
        this.addStyleName(AMXD_SHADOW_BORDER);
    }

    public HorizontalLayout getButtonsPanel() {
        return buttonsPanel;
    }

    @Override
    public void addComponent(Component c) {
        gridLayout.addComponent(c);
    }

    public void addComponent(Component component, int column, int row) {
        gridLayout.addComponent(component, column, row);
    }

    @Override
    public void addLayoutClickListener(LayoutEvents.LayoutClickListener listener) {
        gridLayout.addLayoutClickListener(listener);
    }

    @Override
    public void removeLayoutClickListener(LayoutEvents.LayoutClickListener listener) {
        gridLayout.removeLayoutClickListener(listener);
    }

    @Override
    public void setDropHandler(DropHandler dropHandler) {
        gridLayout.setDropHandler(dropHandler);
    }

    @Override
    public DropHandler getDropHandler() {
        return gridLayout.getDropHandler();
    }

    @Override
    public TargetDetails translateDropTargetDetails(Map<String, Object> clientVariables) {
        return gridLayout.translateDropTargetDetails(clientVariables);
    }

    @Override
    public LayoutDragMode getDragMode() {
        return gridLayout.getDragMode();
    }

    @Override
    public void setDragMode(LayoutDragMode mode) {
        gridLayout.setDragMode(mode);
    }

    @Override
    public DragFilter getDragFilter() {
        return gridLayout.getDragFilter();
    }

    @Override
    public void setDragFilter(DragFilter dragFilter) {
        gridLayout.setDragFilter(dragFilter);
    }

    @Override
    public Transferable getTransferable(Map<String, Object> rawVariables) {
        return gridLayout.getTransferable(rawVariables);
    }
}
