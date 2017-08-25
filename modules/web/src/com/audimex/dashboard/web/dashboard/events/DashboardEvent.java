/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.events;

/**
 * @author bochkarev
 * @version $Id$
 */
public class DashboardEvent<T> {
    protected T entity;

    protected DashboardEventType type;

    public DashboardEvent(T object, DashboardEventType type) {
        this.entity = object;
        this.type = type;
    }

    public DashboardEventType getType() {
        return type;
    }

    public void setType(DashboardEventType type) {
        this.type = type;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }
}
