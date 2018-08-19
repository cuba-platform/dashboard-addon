/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.layouts;


import com.haulmont.addon.dnd.components.DDLayout;
import com.haulmont.addon.dnd.components.DropHandler;
import com.haulmont.addon.dnd.components.dragevent.DropTarget;
import com.haulmont.addon.dnd.components.dragevent.TargetDetails;
import com.haulmont.addon.dnd.components.dragfilter.DragFilter;
import com.haulmont.addon.dnd.components.dragfilter.DragFilterSupport;
import com.haulmont.addon.dnd.components.enums.LayoutDragMode;
import com.haulmont.addon.dnd.web.gui.components.TargetConverter;
import com.haulmont.cuba.gui.components.HBoxLayout;
import com.haulmont.cuba.web.gui.components.WebAbstractComponent;
import com.haulmont.cuba.web.gui.components.WebCssLayout;
import com.haulmont.cuba.web.gui.components.WebHBoxLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.Layout;

import java.util.Map;
import java.util.UUID;

public abstract class AbstractCanvasLayout extends WebCssLayout implements CanvasLayout {
    protected WebAbstractComponent delegate;
    protected HBoxLayout buttonsPanel;
    protected UUID uuid;

    public AbstractCanvasLayout(WebAbstractComponent delegate) {
        this.delegate = delegate;
        buttonsPanel = new WebHBoxLayout();
        super.add(delegate);
        super.add(buttonsPanel);
    }

    @Override
    public WebAbstractComponent getDelegate() {
        return delegate;
    }

    public void setDelegate(WebAbstractComponent delegate) {
        this.delegate = delegate;
    }

    @Override
    public HBoxLayout getButtonsPanel() {
        return buttonsPanel;
    }

    public void setButtonsPanel(HBoxLayout buttonsPanel) {
        this.buttonsPanel = buttonsPanel;
    }

    @Override
    public void setDropHandler(DropHandler dropHandler) {
        ((DDLayout) delegate).setDropHandler(dropHandler);
    }

    @Override
    public DropHandler getDropHandler() {
        return ((DDLayout) delegate).getDropHandler();
    }

    @Override
    public LayoutDragMode getDragMode() {
        return ((DDLayout) delegate).getDragMode();
    }

    @Override
    public void setDragMode(LayoutDragMode mode) {
        ((DDLayout) delegate).setDragMode(mode);
    }

    @Override
    public void addLayoutClickListener(LayoutClickListener listener) {
        ((LayoutClickNotifier) delegate).addLayoutClickListener(listener);
    }

    @Override
    public void removeLayoutClickListener(LayoutClickListener listener) {
        ((LayoutClickNotifier) delegate).removeLayoutClickListener(listener);
    }

    @Override
    public DragFilter getDragFilter() {
        return ((DragFilterSupport) delegate).getDragFilter();
    }

    @Override
    public void setDragFilter(DragFilter dragFilter) {
        ((DragFilterSupport) delegate).setDragFilter(dragFilter);
    }

    @Override
    public TargetDetails convertTargetDetails(com.vaadin.event.dd.TargetDetails details) {
        return ((TargetConverter) delegate).convertTargetDetails(details);
    }

    @Override
    public TargetDetails translateDropTargetDetails(Map<String, Object> clientVariables) {
        return ((DropTarget) delegate).translateDropTargetDetails(clientVariables);
    }

    @Override
    public void setWeight(int weight) {
        Layout unwrapThis = this.unwrap(Layout.class);
        HasComponents parent = unwrapThis.getParent();

        if (parent instanceof AbstractOrderedLayout) {
            for (com.vaadin.ui.Component child : parent) {
                if (unwrapThis.equals(child)) {
                    ((AbstractOrderedLayout) parent).setExpandRatio(unwrapThis, weight);
                } else if (((AbstractOrderedLayout) parent).getExpandRatio(child) == 0) {
                    ((AbstractOrderedLayout) parent).setExpandRatio(child, 1);
                }
            }
        }
    }

    @Override
    public int getWeight() {
        Layout unwrapThis = this.unwrap(Layout.class);
        HasComponents parent = unwrapThis.getParent();

        if (parent instanceof AbstractOrderedLayout) {
            int weight = (int) ((AbstractOrderedLayout) parent).getExpandRatio(unwrapThis);
            return weight > 0 ? weight : 1;
        } else {
            return 1;
        }
    }

    @Override
    public void setSizeFull() {
        super.setSizeFull();
        delegate.setSizeFull();
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
