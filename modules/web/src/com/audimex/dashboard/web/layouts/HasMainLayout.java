package com.audimex.dashboard.web.layouts;

import com.vaadin.ui.AbstractOrderedLayout;

public interface HasMainLayout {
    AbstractOrderedLayout getMainLayout();
    void setMargin(boolean margin);
}
