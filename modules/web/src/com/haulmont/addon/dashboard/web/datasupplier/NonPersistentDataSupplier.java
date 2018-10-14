package com.haulmont.addon.dashboard.web.datasupplier;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.data.impl.GenericDataSupplier;

import javax.annotation.Nullable;

public class NonPersistentDataSupplier extends GenericDataSupplier {

    @Override
    public <E extends Entity> E reload(E entity, View view, @Nullable MetaClass metaClass, boolean loadDynamicAttributes) {
        return entity;
    }
}
