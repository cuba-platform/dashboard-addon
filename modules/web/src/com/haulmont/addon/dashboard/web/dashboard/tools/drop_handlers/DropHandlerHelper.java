/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.dashboard.tools.drop_handlers;

import com.haulmont.addon.dashboard.web.dashboard.frames.editor.vaadin_components.Draggable;
import com.haulmont.addon.dashboard.web.dashboard.layouts.CanvasLayout;
import com.haulmont.addon.dashboard.web.dashboard.tools.DropLayoutTools;
import com.haulmont.cuba.gui.components.BoxLayout;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.addon.dashboard.web.dashboard.tools.DropLayoutTools;

import static com.haulmont.cuba.gui.components.Component.*;

public interface DropHandlerHelper {

    default void addComponent(DropLayoutTools tools, BoxLayout target, Component comp, int idx) {
        if (comp instanceof Draggable) {
            if (idx >= 0) {
                tools.addComponent(target, ((Draggable) comp).getLayout(), idx);
            } else {
                tools.addComponent(target, ((Draggable) comp).getLayout());
            }
        } else {
            OrderedContainer parent = (OrderedContainer) comp.getParent();
            if (parent != null) {
                parent.remove(comp);

                if (idx >= 0) {
                    target.add(comp, idx);
                } else {
                    target.add(comp);
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
