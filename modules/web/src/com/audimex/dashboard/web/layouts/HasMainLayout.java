/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.layouts;

import com.vaadin.ui.AbstractOrderedLayout;

public interface HasMainLayout {
    AbstractOrderedLayout getMainLayout();
    void setMargin(boolean margin);
}
