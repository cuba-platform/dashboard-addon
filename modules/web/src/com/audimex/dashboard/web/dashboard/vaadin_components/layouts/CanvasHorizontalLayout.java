/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.vaadin_components.layouts;

import com.vaadin.event.LayoutEvents;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import fi.jasoft.dragdroplayouts.DDHorizontalLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;
import fi.jasoft.dragdroplayouts.interfaces.DragFilter;

import java.util.Map;

import static com.audimex.dashboard.web.DashboardStyleConstants.*;

public class CanvasHorizontalLayout extends CssLayout implements CanvasLayout {
    protected DDHorizontalLayout horizontalLayout = new DDHorizontalLayout();
    protected HorizontalLayout buttonsPanel = new HorizontalLayout();

    public CanvasHorizontalLayout() {
        super();

//        Button configButton = new Button(WebComponentsHelper.getIcon("icons/gear.png"));
        Button configButton = new Button("Config");
        configButton.addStyleName(AMXD_EDIT_BUTTON);

//        Button removeButton = new Button(WebComponentsHelper.getIcon("icons/trash.png"));
        Button removeButton = new Button("Remove");
        removeButton.addStyleName(AMXD_EDIT_BUTTON);

        buttonsPanel.addComponent(configButton);
        buttonsPanel.addComponent(removeButton);
        buttonsPanel.addStyleName(AMXD_LAYOUT_CONTROLS);

        horizontalLayout.setDragMode(fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode.CLONE);
        horizontalLayout.setSizeFull();
        horizontalLayout.setSpacing(true);
        horizontalLayout.setMargin(true);
        horizontalLayout.addStyleName(AMXD_LAYOUT_CONTENT);

        super.addComponent(buttonsPanel);
        super.addComponent(horizontalLayout);

        this.setDragMode(LayoutDragMode.CLONE);
        this.setSizeFull();
        this.addStyleName(AMXD_SHADOW_BORDER);
    }


    public HorizontalLayout getButtonsPanel() {
        return buttonsPanel;
    }

    @Override
    public void addComponent(Component c) {
        horizontalLayout.addComponent(c);
    }

    @Override
    public void addComponentAsFirst(Component c) {
        horizontalLayout.addComponentAsFirst(c);
    }

    @Override
    public void addComponent(Component c, int index) {
        horizontalLayout.addComponent(c, index);
    }

    @Override
    public void addLayoutClickListener(LayoutEvents.LayoutClickListener listener) {
        horizontalLayout.addLayoutClickListener(listener);
    }

    @Override
    public void removeLayoutClickListener(LayoutEvents.LayoutClickListener listener) {
        horizontalLayout.removeLayoutClickListener(listener);
    }

    @Override
    public DropHandler getDropHandler() {
        return horizontalLayout.getDropHandler();
    }

    @Override
    public void setDropHandler(DropHandler dropHandler) {
        horizontalLayout.setDropHandler(dropHandler);
    }

    @Override
    public TargetDetails translateDropTargetDetails(Map<String, Object> map) {
        return horizontalLayout.translateDropTargetDetails(map);
    }

    @Override
    public LayoutDragMode getDragMode() {
        return horizontalLayout.getDragMode();
    }

    @Override
    public void setDragMode(LayoutDragMode layoutDragMode) {
        horizontalLayout.setDragMode(layoutDragMode);
    }

    @Override
    public DragFilter getDragFilter() {
        return horizontalLayout.getDragFilter();
    }

    @Override
    public void setDragFilter(DragFilter dragFilter) {
        horizontalLayout.setDragFilter(dragFilter);
    }

    @Override
    public Transferable getTransferable(Map<String, Object> map) {
        return horizontalLayout.getTransferable(map);
    }
}
