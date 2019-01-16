package com.haulmont.addon.dashboard.model.visualmodel;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

@MetaClass(name = "dashboard$ResponsiveLayout")
public class ResponsiveLayout extends DashboardLayout {

    @MetaProperty
    protected Set<ResponsiveArea> areas = new HashSet<>();

    @Override
    public String getCaption() {
        Messages messages = AppBeans.get(Messages.class);
        return messages.getMessage(getClass(), "Layout.responsive");
    }

    public Set<ResponsiveArea> getAreas() {
        return areas;
    }

    public void setAreas(Set<ResponsiveArea> areas) {
        this.areas = areas;
    }

    @Override
    public void addChild(DashboardLayout child) {
        Integer order = getAreas().stream().map(ResponsiveArea::getOrder).max(Comparator.naturalOrder()).orElse(0) + 1;
        Metadata metadata = AppBeans.get(Metadata.class);
        ResponsiveArea ra = metadata.create(ResponsiveArea.class);
        ra.setOrder(order);
        ra.setComponent(child);
        getAreas().add(ra);
    }


}
