/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.layouts;

import com.vaadin.event.LayoutEvents;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragSource;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.ui.*;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;
import fi.jasoft.dragdroplayouts.interfaces.DragFilter;
import fi.jasoft.dragdroplayouts.interfaces.LayoutDragSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public abstract class AbstractCanvasLayout extends CssLayout implements CanvasLayout {
    protected AbstractLayout delegate;
    protected HorizontalLayout buttonsPanel = new HorizontalLayout();

    public AbstractCanvasLayout(AbstractLayout delegate) {
        this.delegate = delegate;
        super.addComponent(buttonsPanel);
        super.addComponent(delegate);
    }

    public AbstractLayout getDelegate() {
        return delegate;
    }

    public HorizontalLayout getButtonsPanel() {
        return buttonsPanel;
    }

    @Override
    public void addComponent(Component c) {
        delegate.addComponent(c);
    }

    @Override
    public void setDropHandler(DropHandler dropHandler) {
        try {
            Method method = delegate.getClass().getMethod("setDropHandler", DropHandler.class);
            method.invoke(delegate, dropHandler);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(String.format("Can't execute method setDropHandler() for class %s", delegate.getClass().getName()), e);
        }
    }

    @Override
    public DropHandler getDropHandler() {
        return ((DropTarget) delegate).getDropHandler();
    }

    @Override
    public void addLayoutClickListener(LayoutEvents.LayoutClickListener listener) {
        ((LayoutEvents.LayoutClickNotifier) delegate).addLayoutClickListener(listener);
    }

    @Override
    public void removeLayoutClickListener(LayoutEvents.LayoutClickListener listener) {
        ((LayoutEvents.LayoutClickNotifier) delegate).removeLayoutClickListener(listener);
    }

    @Override
    public TargetDetails translateDropTargetDetails(Map<String, Object> clientVariables) {
        return ((DropTarget) delegate).translateDropTargetDetails(clientVariables);
    }

    @Override
    public LayoutDragMode getDragMode() {
        return ((LayoutDragSource) delegate).getDragMode();
    }

    @Override
    public void setDragMode(LayoutDragMode mode) {
        ((LayoutDragSource) delegate).setDragMode(mode);
    }

    @Override
    public DragFilter getDragFilter() {
        return ((LayoutDragSource) delegate).getDragFilter();
    }

    @Override
    public void setDragFilter(DragFilter dragFilter) {
        ((LayoutDragSource) delegate).setDragFilter(dragFilter);
    }

    @Override
    public Transferable getTransferable(Map<String, Object> rawVariables) {
        return ((DragSource) delegate).getTransferable(rawVariables);
    }

    @Override
    public void setWeight(int weight) {
        if (getParent() instanceof AbstractOrderedLayout) {
            AbstractOrderedLayout parent = (AbstractOrderedLayout) getParent();

            for (Component component : parent) {
                if (component.equals(this)) {
                    parent.setExpandRatio(this, weight);
                } else if (parent.getExpandRatio(component) == 0) {
                    parent.setExpandRatio(component, 1);
                }
            }
        }
    }

    @Override
    public int getWeight() {
        if (getParent() instanceof AbstractOrderedLayout) {
            int weight = (int) ((AbstractOrderedLayout) getParent()).getExpandRatio(this);
            return weight > 0 ? weight : 1;
        }
        return 1;
    }
}
