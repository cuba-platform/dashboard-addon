/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.config.widget_types_config;

import com.audimex.dashboard.config.widget_types_config.WidgetTypeInfo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class WidgetTypesInfo implements List<WidgetTypeInfo>, Serializable {

    protected List<WidgetTypeInfo> delegate;

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    @Override
    public Iterator<WidgetTypeInfo> iterator() {
        return delegate.iterator();
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return delegate.toArray(a);
    }

    @Override
    public boolean add(WidgetTypeInfo widgetTypeInfo) {
        return delegate.add(widgetTypeInfo);
    }

    @Override
    public boolean remove(Object o) {
        return delegate.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends WidgetTypeInfo> c) {
        return delegate.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends WidgetTypeInfo> c) {
        return delegate.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return delegate.retainAll(c);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public WidgetTypeInfo get(int index) {
        return delegate.get(index);
    }

    @Override
    public WidgetTypeInfo set(int index, WidgetTypeInfo element) {
        return delegate.set(index, element);
    }

    @Override
    public void add(int index, WidgetTypeInfo element) {
        delegate.add(index, element);
    }

    @Override
    public WidgetTypeInfo remove(int index) {
        return delegate.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return delegate.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return delegate.lastIndexOf(o);
    }

    @Override
    public ListIterator<WidgetTypeInfo> listIterator() {
        return delegate.listIterator();
    }

    @Override
    public ListIterator<WidgetTypeInfo> listIterator(int index) {
        return delegate.listIterator(index);
    }

    @Override
    public List<WidgetTypeInfo> subList(int fromIndex, int toIndex) {
        return delegate.subList(fromIndex, toIndex);
    }
}
