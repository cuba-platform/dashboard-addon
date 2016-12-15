/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.drophandlers;

import com.audimex.dashboard.entity.DropTarget;
import com.haulmont.bali.datastruct.Tree;

public interface StructureChangeListener {
    void structureChanged(Tree tree, DropTarget dropTarget);
}