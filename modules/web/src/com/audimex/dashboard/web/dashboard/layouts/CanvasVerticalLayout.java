/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.layouts;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.web.gui.icons.IconResolver;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import fi.jasoft.dragdroplayouts.DDVerticalLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;
import fi.jasoft.dragdroplayouts.interfaces.DragFilter;

import java.util.Map;

import static com.audimex.dashboard.web.DashboardIcon.GEAR_ICON;
import static com.audimex.dashboard.web.DashboardIcon.TRASH_ICON;
import static com.audimex.dashboard.web.DashboardStyleConstants.*;

public class CanvasVerticalLayout extends CssLayout implements CanvasLayout {
    protected DDVerticalLayout verticalLayout = new DDVerticalLayout();
    protected HorizontalLayout buttonsPanel = new HorizontalLayout();

    public CanvasVerticalLayout() {
        super();
        //todo move creation to factory
        Button configButton = new Button("Config");
        configButton.addStyleName(AMXD_EDIT_BUTTON);

        Button removeButton = new Button("Remove");
        removeButton.addStyleName(AMXD_EDIT_BUTTON);

        IconResolver iconResolver = AppBeans.get(IconResolver.class);
        removeButton.setIcon(iconResolver.getIconResource(TRASH_ICON.source()));
        configButton.setIcon(iconResolver.getIconResource(GEAR_ICON.source()));

        buttonsPanel.addComponent(configButton);
        buttonsPanel.addComponent(removeButton);
        buttonsPanel.addStyleName(AMXD_LAYOUT_CONTROLS);

        verticalLayout.setDragMode(fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode.CLONE);
        verticalLayout.setSizeFull();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(true);
        verticalLayout.addStyleName(AMXD_LAYOUT_CONTENT);

        super.addComponent(buttonsPanel);
        super.addComponent(verticalLayout);

        this.setDragMode(LayoutDragMode.CLONE);
        this.setSizeFull();
        this.addStyleName(AMXD_SHADOW_BORDER);
    }


    public HorizontalLayout getButtonsPanel() {
        return buttonsPanel;
    }

    @Override
    public void addComponent(Component c) {
        verticalLayout.addComponent(c);
    }

    @Override
    public void addComponentAsFirst(Component c) {
        verticalLayout.addComponentAsFirst(c);
    }

    @Override
    public void addComponent(Component c, int index) {
        verticalLayout.addComponent(c, index);
    }

    @Override
    public void addLayoutClickListener(LayoutEvents.LayoutClickListener listener) {
        verticalLayout.addLayoutClickListener(listener);
    }

    @Override
    public void removeLayoutClickListener(LayoutEvents.LayoutClickListener listener) {
        verticalLayout.removeLayoutClickListener(listener);
    }

    @Override
    public DropHandler getDropHandler() {
        return verticalLayout.getDropHandler();
    }

    @Override
    public void setDropHandler(DropHandler dropHandler) {
        verticalLayout.setDropHandler(dropHandler);
    }

    @Override
    public TargetDetails translateDropTargetDetails(Map<String, Object> map) {
        return verticalLayout.translateDropTargetDetails(map);
    }

    @Override
    public LayoutDragMode getDragMode() {
        return verticalLayout.getDragMode();
    }

    @Override
    public void setDragMode(LayoutDragMode layoutDragMode) {
        verticalLayout.setDragMode(layoutDragMode);
    }

    @Override
    public DragFilter getDragFilter() {
        return verticalLayout.getDragFilter();
    }

    @Override
    public void setDragFilter(DragFilter dragFilter) {
        verticalLayout.setDragFilter(dragFilter);
    }

    @Override
    public Transferable getTransferable(Map<String, Object> map) {
        return verticalLayout.getTransferable(map);
    }
}
