/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.tools.drop_handlers;

import com.audimex.dashboard.web.dashboard.frames.editor.vaadin_components.Draggable;
import com.audimex.dashboard.web.dashboard.layouts.CanvasLayout;
import com.audimex.dashboard.web.dashboard.tools.DropLayoutTools;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;

public interface DropHandlerHelper {

    default void addComponent(DropLayoutTools tools, AbstractOrderedLayout layout, Component comp, int idx) {
        if (comp instanceof Draggable) {
            if (idx >= 0) {
                tools.addComponent(layout, ((Draggable) comp).getLayout(), idx);
            } else {
                tools.addComponent(layout, ((Draggable) comp).getLayout());
            }
        } else {
            CanvasLayout parent = getCanvasLayoutParent(comp);
            if (parent != null) {
                if (idx >= 0) {
                    layout.addComponent(parent, idx);
                } else {
                    layout.addComponent(parent);
                }
            }
        }
    }

    default CanvasLayout getCanvasLayoutParent(Component comp) {
        if (comp == null) {
            return null;
        } else if (comp instanceof CanvasLayout) {
            return (CanvasLayout) comp;
        } else {
            return getCanvasLayoutParent(comp.getParent());
        }
    }
}
