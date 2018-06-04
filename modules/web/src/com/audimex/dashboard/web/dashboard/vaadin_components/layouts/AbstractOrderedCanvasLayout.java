/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.vaadin_components.layouts;

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;

public class AbstractOrderedCanvasLayout extends AbstractCanvasLayout {

    public AbstractOrderedCanvasLayout(AbstractLayout delegate) {
        super(delegate);
    }

    @Override
    public void addComponentAsFirst(Component c) {
        ((AbstractOrderedLayout) delegate).addComponentAsFirst(c);
    }

    @Override
    public void addComponent(Component c, int index) {
        ((AbstractOrderedLayout) delegate).addComponent(c, index);
    }
}
